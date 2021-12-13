package com.example.medicinetime.data.source.local

import android.content.Context
import android.database.Cursor
import com.example.medicinetime.data.source.History
import java.net.URISyntaxException
import java.util.*


class MedicineDBHelper
/**
 * Constructor
 */
    (context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    /** Creating tables  */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_PILL_TABLE)
        db.execSQL(CREATE_ALARM_TABLE)
        db.execSQL(CREATE_PILL_ALARM_LINKS_TABLE)
        db.execSQL(CREATE_HISTORIES_TABLE)
    }

    // TODO: change this so that updating doesn't delete old data
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + PILL_TABLE)
        db.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE)
        db.execSQL("DROP TABLE IF EXISTS " + PILL_ALARM_LINKS)
        db.execSQL("DROP TABLE IF EXISTS " + HISTORIES_TABLE)
        onCreate(db)
    }
    // ############################## create methods ###################################### //
    /**
     * createPill takes a pill object and inserts the relevant data into the database
     *
     * @param pill a model pill object
     * @return the long row_id generate by the database upon entry into the database
     */
    fun createPill(pill: Pills): Long {
        val db: SQLiteDatabase = this.getWritableDatabase()
        val values = ContentValues()
        values.put(KEY_PILLNAME, pill.getPillName())
        return db.insert(PILL_TABLE, null, values)
    }

    /**
     * takes in a model alarm object and inserts a row into the database
     * for each day of the week the alarm is meant to go off.
     *
     * @param alarm   a model alarm object
     * @param pill_id the id associated with the pill the alarm is for
     * @return a array of longs that are the row_ids generated by the database when the rows are inserted
     */
    fun createAlarm(alarm: MedicineAlarm, pill_id: Long): LongArray {
        val db: SQLiteDatabase = this.getWritableDatabase()
        val alarm_ids = LongArray(7)
        /** Create a separate row in the table for every day of the week for this alarm  */
        var arrayPos = 0
        for (day in alarm.getDayOfWeek()) {
            if (day) {
                val values = ContentValues()
                values.put(KEY_HOUR, alarm.getHour())
                values.put(KEY_MINUTE, alarm.getMinute())
                values.put(KEY_DAY_WEEK, arrayPos + 1)
                values.put(KEY_ALARMS_PILL_NAME, alarm.getPillName())
                values.put(KEY_DOSE_QUANTITY, alarm.getDoseQuantity())
                values.put(KEY_DOSE_UNITS, alarm.getDoseUnit())
                values.put(KEY_DATE_STRING, alarm.getDateString())
                values.put(KEY_ALARM_ID, alarm.getAlarmId())
                /** Insert row  */
                val alarm_id: Long = db.insert(ALARM_TABLE, null, values)
                alarm_ids[arrayPos] = alarm_id
                /** Link alarm to a pill  */
                createPillAlarmLink(pill_id, alarm_id)
            }
            arrayPos++
        }
        return alarm_ids
    }

    /**
     * private function that inserts a row into a table that links pills and alarms
     *
     * @param pill_id  the row_id of the pill that is being added to or edited
     * @param alarm_id the row_id of the alarm that is being added to the pill
     * @return returns the row_id the database creates when a row is created
     */
    private fun createPillAlarmLink(pill_id: Long, alarm_id: Long): Long {
        val db: SQLiteDatabase = this.getWritableDatabase()
        val values = ContentValues()
        values.put(KEY_PILLTABLE_ID, pill_id)
        values.put(KEY_ALARMTABLE_ID, alarm_id)
        return db.insert(PILL_ALARM_LINKS, null, values)
    }

    /**
     * uses a history model object to store histories in the DB
     *
     * @param history a history model object
     */
    fun createHistory(history: History) {
        val db: SQLiteDatabase = getWritableDatabase()
        val values = ContentValues()
        values.put(KEY_PILLNAME, history.getPillName())
        values.put(KEY_DATE_STRING, history.getDateString())
        values.put(KEY_HOUR, history.getHourTaken())
        values.put(KEY_MINUTE, history.getMinuteTaken())
        values.put(KEY_DOSE_QUANTITY, history.getDoseQuantity())
        values.put(KEY_DOSE_UNITS, history.getDoseUnit())
        values.put(KEY_ACTION, history.getAction())
        values.put(KEY_ALARM_ID, history.getAlarmId())
        /** Insert row  */
        db.insert(HISTORIES_TABLE, null, values)
    }
    // ############################# get methods ####################################### //
    /**
     * allows pillBox to retrieve a row from pill table in Db
     *
     * @param pillName takes in a string of the pill Name
     * @return returns a pill model object
     */
    fun getPillByName(pillName: String): Pills {
        val db: SQLiteDatabase = this.getReadableDatabase()
        val dbPill = ("select * from "
                + PILL_TABLE + " where "
                + KEY_PILLNAME + " = "
                + "'" + pillName + "'")
        val c: Cursor = db.rawQuery(dbPill, null)
        val pill = Pills()
        if (c.moveToFirst() && c.count >= 1) {
            pill.setPillName(c.getString(c.getColumnIndex(KEY_PILLNAME)))
            pill.setPillId(c.getLong(c.getColumnIndex(KEY_ROWID)))
            c.close()
        }
        return pill
    }
    /** Loops through all rows, adds to list  */
    /**
     * allows the pillBox to retrieve all the pill rows from database
     *
     * @return a list of pill model objects
     */
    val allPills: List<Any>
        get() {
            val pills: MutableList<Pills> = ArrayList<Pills>()
            val dbPills = "SELECT * FROM " + PILL_TABLE
            val db: SQLiteDatabase = getReadableDatabase()
            val c: Cursor = db.rawQuery(dbPills, null)
            /** Loops through all rows, adds to list  */
            if (c.moveToFirst()) {
                do {
                    val p = Pills()
                    p.setPillName(c.getString(c.getColumnIndex(KEY_PILLNAME)))
                    p.setPillId(c.getLong(c.getColumnIndex(KEY_ROWID)))
                    pills.add(p)
                } while (c.moveToNext())
            }
            c.close()
            return pills
        }

    /**
     * Allows pillBox to retrieve all Alarms linked to a Pill
     * uses combineAlarms helper method
     *
     * @param pillName string
     * @return list of alarm objects
     * @throws URISyntaxException honestly do not know why, something about alarm.getDayOfWeek()
     */
    @Throws(URISyntaxException::class)
    fun getAllAlarmsByPill(pillName: String): List<MedicineAlarm> {
        val alarmsByPill: MutableList<MedicineAlarm> = ArrayList<MedicineAlarm>()
        /** HINT: When reading string: '.' are not periods ex) pill.rowIdNumber  */
        val selectQuery = "SELECT * FROM " +
                ALARM_TABLE + " alarm, " +
                PILL_TABLE + " pill, " +
                PILL_ALARM_LINKS + " pillAlarm WHERE " +
                "pill." + KEY_PILLNAME + " = '" + pillName + "'" +
                " AND pill." + KEY_ROWID + " = " +
                "pillAlarm." + KEY_PILLTABLE_ID +
                " AND alarm." + KEY_ROWID + " = " +
                "pillAlarm." + KEY_ALARMTABLE_ID
        val db: SQLiteDatabase = this.getReadableDatabase()
        val c: Cursor = db.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                val al = MedicineAlarm()
                al.setId(c.getInt(c.getColumnIndex(KEY_ROWID)))
                al.setHour(c.getInt(c.getColumnIndex(KEY_HOUR)))
                al.setMinute(c.getInt(c.getColumnIndex(KEY_MINUTE)))
                al.setPillName(c.getString(c.getColumnIndex(KEY_ALARMS_PILL_NAME)))
                al.setDoseQuantity(c.getString(c.getColumnIndex(KEY_DOSE_QUANTITY)))
                al.setDoseUnit(c.getString(c.getColumnIndex(KEY_DOSE_UNITS)))
                al.setDateString(c.getString(c.getColumnIndex(KEY_DATE_STRING)))
                al.setAlarmId(c.getInt(c.getColumnIndex(KEY_ALARM_ID)))
                alarmsByPill.add(al)
            } while (c.moveToNext())
        }
        c.close()
        return combineAlarms(alarmsByPill)
    }

    @Throws(URISyntaxException::class)
    fun getAllAlarms(pillName: String): List<MedicineAlarm> {
        val alarmsByPill: MutableList<MedicineAlarm> = ArrayList<MedicineAlarm>()
        /** HINT: When reading string: '.' are not periods ex) pill.rowIdNumber  */
        val selectQuery = "SELECT * FROM " +
                ALARM_TABLE + " alarm, " +
                PILL_TABLE + " pill, " +
                PILL_ALARM_LINKS + " pillAlarm WHERE " +
                "pill." + KEY_PILLNAME + " = '" + pillName + "'" +
                " AND pill." + KEY_ROWID + " = " +
                "pillAlarm." + KEY_PILLTABLE_ID +
                " AND alarm." + KEY_ROWID + " = " +
                "pillAlarm." + KEY_ALARMTABLE_ID
        val db: SQLiteDatabase = this.getReadableDatabase()
        val c: Cursor = db.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                val al = MedicineAlarm()
                al.setId(c.getInt(c.getColumnIndex(KEY_ROWID)))
                al.setHour(c.getInt(c.getColumnIndex(KEY_HOUR)))
                al.setMinute(c.getInt(c.getColumnIndex(KEY_MINUTE)))
                al.setPillName(c.getString(c.getColumnIndex(KEY_ALARMS_PILL_NAME)))
                al.setDoseQuantity(c.getString(c.getColumnIndex(KEY_DOSE_QUANTITY)))
                al.setDoseUnit(c.getString(c.getColumnIndex(KEY_DOSE_UNITS)))
                al.setDateString(c.getString(c.getColumnIndex(KEY_DATE_STRING)))
                al.setAlarmId(c.getInt(c.getColumnIndex(KEY_ALARM_ID)))
                alarmsByPill.add(al)
            } while (c.moveToNext())
        }
        c.close()
        return alarmsByPill
    }

    /**
     * returns all individual alarms that occur on a certain day of the week,
     * alarms returned do not know of their counterparts that occur on different days
     *
     * @param day an integer that represents the day of week
     * @return a list of Alarms (not combined into full-model-alarms)
     */
    fun getAlarmsByDay(day: Int): List<MedicineAlarm> {
        val daysAlarms: MutableList<MedicineAlarm> = ArrayList<MedicineAlarm>()
        val selectQuery = "SELECT * FROM " +
                ALARM_TABLE + " alarm WHERE " +
                "alarm." + KEY_DAY_WEEK +
                " = '" + day + "'"
        val db: SQLiteDatabase = getReadableDatabase()
        val c: Cursor = db.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                val al = MedicineAlarm()
                al.setId(c.getInt(c.getColumnIndex(KEY_ROWID)))
                al.setHour(c.getInt(c.getColumnIndex(KEY_HOUR)))
                al.setMinute(c.getInt(c.getColumnIndex(KEY_MINUTE)))
                al.setPillName(c.getString(c.getColumnIndex(KEY_ALARMS_PILL_NAME)))
                al.setDoseQuantity(c.getString(c.getColumnIndex(KEY_DOSE_QUANTITY)))
                al.setDoseUnit(c.getString(c.getColumnIndex(KEY_DOSE_UNITS)))
                al.setDateString(c.getString(c.getColumnIndex(KEY_DATE_STRING)))
                al.setAlarmId(c.getInt(c.getColumnIndex(KEY_ALARM_ID)))
                daysAlarms.add(al)
            } while (c.moveToNext())
        }
        c.close()
        return daysAlarms
    }

    /**
     * @param alarm_id
     * @return
     * @throws URISyntaxException
     */
    @Throws(URISyntaxException::class)
    fun getAlarmById(alarm_id: Long): MedicineAlarm {
        val dbAlarm = "SELECT * FROM " +
                ALARM_TABLE + " WHERE " +
                KEY_ROWID + " = " + alarm_id
        val db: SQLiteDatabase = getReadableDatabase()
        val c: Cursor = db.rawQuery(dbAlarm, null)
        if (c != null) c.moveToFirst()
        val al = MedicineAlarm()
        al.setId(c.getInt(c.getColumnIndex(KEY_ROWID)))
        al.setHour(c.getInt(c.getColumnIndex(KEY_HOUR)))
        al.setMinute(c.getInt(c.getColumnIndex(KEY_MINUTE)))
        al.setPillName(c.getString(c.getColumnIndex(KEY_ALARMS_PILL_NAME)))
        al.setDoseQuantity(c.getString(c.getColumnIndex(KEY_DOSE_QUANTITY)))
        al.setDoseUnit(c.getString(c.getColumnIndex(KEY_DOSE_UNITS)))
        al.setDateString(c.getString(c.getColumnIndex(KEY_DATE_STRING)))
        al.setAlarmId(c.getInt(c.getColumnIndex(KEY_ALARM_ID)))
        c.close()
        return al
    }

    /**
     * Private helper function that combines rows in the databse back into a
     * full model-alarm with a dayOfWeek array.
     *
     * @param dbAlarms a list of dbAlarms (not-full-alarms w/out day of week info)
     * @return a list of model-alarms
     * @throws URISyntaxException
     */
    @Throws(URISyntaxException::class)
    private fun combineAlarms(dbAlarms: List<MedicineAlarm>): List<MedicineAlarm> {
        val timesOfDay: MutableList<String> = ArrayList()
        val combinedAlarms: MutableList<MedicineAlarm> = ArrayList<MedicineAlarm>()
        for (al in dbAlarms) {
            if (timesOfDay.contains(al.getStringTime())) {
                /** Add this db row to alarm object  */
                for (ala in combinedAlarms) {
                    if (ala.getStringTime().equals(al.getStringTime())) {
                        val day = getDayOfWeek(al.getId())
                        val days: BooleanArray = ala.getDayOfWeek()
                        days[day - 1] = true
                        ala.setDayOfWeek(days)
                        ala.addId(al.getId())
                    }
                }
            } else {
                /** Create new Alarm object with day of week array  */
                val newAlarm = MedicineAlarm()
                val days = BooleanArray(7)
                newAlarm.setPillName(al.getPillName())
                newAlarm.setMinute(al.getMinute())
                newAlarm.setHour(al.getHour())
                newAlarm.addId(al.getId())
                newAlarm.setDateString(al.getDateString())
                newAlarm.setAlarmId(al.getAlarmId())
                val day = getDayOfWeek(al.getId())
                days[day - 1] = true
                newAlarm.setDayOfWeek(days)
                timesOfDay.add(al.getStringTime())
                combinedAlarms.add(newAlarm)
            }
        }
        Collections.sort(combinedAlarms)
        return combinedAlarms
    }

    /**
     * Get a single pillapp.Model-Alarm
     * Used as a helper function
     */
    @Throws(URISyntaxException::class)
    fun getDayOfWeek(alarm_id: Long): Int {
        val db: SQLiteDatabase = this.getReadableDatabase()
        val dbAlarm = "SELECT * FROM " +
                ALARM_TABLE + " WHERE " +
                KEY_ROWID + " = " + alarm_id
        val c: Cursor = db.rawQuery(dbAlarm, null)
        if (c != null) c.moveToFirst()
        val dayOfWeek = c.getInt(c.getColumnIndex(KEY_DAY_WEEK))
        c.close()
        return dayOfWeek
    }

    /**
     * allows pillBox to retrieve from History table
     *
     * @return a list of all history objects
     */
    val history: List<com.example.medicinetime.data.source.History>
        get() {
            val allHistory: MutableList<History> = ArrayList<History>()
            val dbHist = "SELECT * FROM " + HISTORIES_TABLE
            val db: SQLiteDatabase = getReadableDatabase()
            val c: Cursor = db.rawQuery(dbHist, null)
            if (c.moveToFirst()) {
                do {
                    val h = History()
                    h.setPillName(c.getString(c.getColumnIndex(KEY_PILLNAME)))
                    h.setDateString(c.getString(c.getColumnIndex(KEY_DATE_STRING)))
                    h.setHourTaken(c.getInt(c.getColumnIndex(KEY_HOUR)))
                    h.setMinuteTaken(c.getInt(c.getColumnIndex(KEY_MINUTE)))
                    h.setDoseQuantity(c.getString(c.getColumnIndex(KEY_DOSE_QUANTITY)))
                    h.setDoseUnit(c.getString(c.getColumnIndex(KEY_DOSE_UNITS)))
                    h.setAction(c.getInt(c.getColumnIndex(KEY_ACTION)))
                    h.setAlarmId(c.getInt(c.getColumnIndex(KEY_ALARM_ID)))
                    allHistory.add(h)
                } while (c.moveToNext())
            }
            c.close()
            return allHistory
        }

    // ############################### delete methods##################################### //
    private fun deletePillAlarmLinks(alarmId: Long) {
        val db: SQLiteDatabase = this.getWritableDatabase()
        db.delete(
            PILL_ALARM_LINKS, KEY_ALARMTABLE_ID
                    + " = ?", arrayOf(alarmId.toString())
        )
    }

    fun deleteAlarm(alarmId: Long) {
        val db: SQLiteDatabase = this.getWritableDatabase()
        /** First delete any link in PillAlarmLink Table  */
        deletePillAlarmLinks(alarmId)

        /* Then delete alarm */db.delete(
            ALARM_TABLE, KEY_ROWID
                    + " = ?", arrayOf(alarmId.toString())
        )
    }

    @Throws(URISyntaxException::class)
    fun deletePill(pillName: String) {
        val db: SQLiteDatabase = this.getWritableDatabase()
        val pillsAlarms: List<MedicineAlarm>
        /** First get all Alarms and delete them and their Pill-links  */
        pillsAlarms = getAllAlarmsByPill(pillName)
        for (alarm in pillsAlarms) {
            val id: Long = alarm.getId()
            deleteAlarm(id)
        }
        /** Then delete Pill  */
        db.delete(
            PILL_TABLE, KEY_PILLNAME
                    + " = ?", arrayOf(pillName)
        )
    }

    companion object {
        /**
         * Database name
         */
        private const val DATABASE_VERSION = 1

        /**
         * Database version
         */
        private const val DATABASE_NAME = "MedicineAlarm.db"

        /**
         * Table names
         */
        private const val PILL_TABLE = "pills"
        private const val ALARM_TABLE = "alarms"
        private const val PILL_ALARM_LINKS = "pill_alarm"
        private const val HISTORIES_TABLE = "histories"

        /**
         * Common column name and location
         */
        const val KEY_ROWID = "id"

        /**
         * Pill table columns, used by History Table
         */
        private const val KEY_PILLNAME = "pillName"

        /**
         * Alarm table columns, Hour & Minute used by History Table
         */
        private const val KEY_INTENT = "intent"
        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"
        private const val KEY_DAY_WEEK = "day_of_week"
        private const val KEY_ALARMS_PILL_NAME = "pillName"
        private const val KEY_DOSE_QUANTITY = "dose_quantity"
        private const val KEY_DOSE_UNITS = "dose_units"
        private const val KEY_ALARM_ID = "alarm_id"

        /**
         * Pill-Alarm link table columns
         */
        private const val KEY_PILLTABLE_ID = "pill_id"
        private const val KEY_ALARMTABLE_ID = "alarm_id"

        /**
         * History Table columns, some used above
         */
        private const val KEY_DATE_STRING = "date"
        private const val KEY_ACTION = "action"

        /**
         * Pill Table: create statement
         */
        private const val CREATE_PILL_TABLE = ("create table " + PILL_TABLE + "("
                + KEY_ROWID + " integer primary key not null,"
                + KEY_PILLNAME + " text not null" + ")")

        /**
         * Alarm Table: create statement
         */
        private const val CREATE_ALARM_TABLE = ("create table " + ALARM_TABLE + "("
                + KEY_ROWID + " integer primary key,"
                + KEY_ALARM_ID + " integer,"
                + KEY_HOUR + " integer,"
                + KEY_MINUTE + " integer,"
                + KEY_ALARMS_PILL_NAME + " text not null,"
                + KEY_DATE_STRING + " text,"
                + KEY_DOSE_QUANTITY + " text,"
                + KEY_DOSE_UNITS + " text,"
                + KEY_DAY_WEEK + " integer" + ")")

        /**
         * Pill-Alarm link table: create statement
         */
        private const val CREATE_PILL_ALARM_LINKS_TABLE = ("create table " + PILL_ALARM_LINKS + "("
                + KEY_ROWID + " integer primary key not null,"
                + KEY_PILLTABLE_ID + " integer not null,"
                + KEY_ALARMTABLE_ID + " integer not null" + ")")

        /**
         * Histories Table: create statement
         */
        private val CREATE_HISTORIES_TABLE = String.format(
            "CREATE TABLE %s(%s integer primary key, %s text not null, %s text, %s text, %s text, %s integer, %s integer, %s integer , %s integer)",
            HISTORIES_TABLE,
            KEY_ROWID,
            KEY_PILLNAME,
            KEY_DOSE_QUANTITY,
            KEY_DOSE_UNITS,
            KEY_DATE_STRING,
            KEY_HOUR,
            KEY_ACTION,
            KEY_MINUTE,
            KEY_ALARM_ID
        )
    }
}