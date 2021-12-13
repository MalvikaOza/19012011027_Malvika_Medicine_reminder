package com.example.medicinetime.data

import androidx.annotation.VisibleForTesting
import com.example.medicinetime.data.source.History
import java.text.SimpleDateFormat
import java.util.*


class FakeMedicineLocalDataSource  //Prevent from direct Instantiation
private constructor() : MedicineDataSource {
    companion object {
        private var INSTANCE: FakeMedicineLocalDataSource? = null
        private val MEDICINE_SERVICE_DATA: MutableMap<String, MedicineAlarm>? = null
        private val HISTORY_SERVICE_DATA: MutableMap<String, History>? = null
        private val PILLS_SERVICE_DATA: MutableMap<String, Pills>? = null
        private fun addMedicine(
            id: Long,
            hour: Int,
            minute: Int,
            pillName: String,
            doseQuantity: String,
            doseUnit: String,
            alarmId: Int
        ) {
            val medicineAlarm =
                MedicineAlarm(id, hour, minute, pillName, doseQuantity, doseUnit, alarmId)
            MEDICINE_SERVICE_DATA!![id.toString()] =
                medicineAlarm
        }

        private fun addHistory(
            hourTaken: Int,
            minuteTaken: Int,
            dateString: String,
            pillName: String,
            action: Int,
            doseQuantity: String,
            doseUnit: String,
            alarmId: Int
        ) {
            val history = History(
                hourTaken,
                minuteTaken,
                dateString,
                pillName,
                action,
                doseQuantity,
                doseUnit,
                alarmId
            )
            HISTORY_SERVICE_DATA!![pillName] = history
        }

        private fun addPills(pillName: String, pillId: Long) {
            val pills = Pills(pillName, pillId)
            PILLS_SERVICE_DATA!![pillId.toString()] =
                pills
        }

        val instance: FakeMedicineLocalDataSource?
            get() {
                if (INSTANCE == null) {
                    INSTANCE = FakeMedicineLocalDataSource()
                }
                return INSTANCE
            }

        init {
            MEDICINE_SERVICE_DATA = LinkedHashMap<String, MedicineAlarm>()
            HISTORY_SERVICE_DATA = LinkedHashMap<String, History>()
            PILLS_SERVICE_DATA = LinkedHashMap<String, Pills>()
            val mCurrentTime = Calendar.getInstance()
            val hour: Int = com.example.medicinetime.data.mCurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = com.example.medicinetime.data.mCurrentTime.get(Calendar.MINUTE)
            val date: Date = com.example.medicinetime.data.mCurrentTime.getTime()
            val dateString = SimpleDateFormat(
                "MMM d, yyyy",
                Locale.getDefault()
            ).format(com.example.medicinetime.data.date)
            addPills("Paracetamol", 1)
            addPills("Crocin", 2)
            val alarmId = Random().nextInt(100)
            addMedicine(
                1,
                com.example.medicinetime.data.hour,
                com.example.medicinetime.data.minute,
                "Paracetamol",
                "1.0",
                "tablet(s)",
                com.example.medicinetime.data.alarmId
            )
            addMedicine(
                2,
                com.example.medicinetime.data.hour + 2,
                com.example.medicinetime.data.minute + 1,
                "Crocin",
                "2.0",
                "capsule(s)",
                com.example.medicinetime.data.alarmId
            )
            addHistory(
                com.example.medicinetime.data.hour,
                com.example.medicinetime.data.minute,
                com.example.medicinetime.data.dateString,
                "Crocin",
                2,
                "2.0",
                "capsule(s)",
                com.example.medicinetime.data.alarmId
            )
            addHistory(
                com.example.medicinetime.data.hour + 2,
                com.example.medicinetime.data.minute + 1,
                com.example.medicinetime.data.dateString,
                "Paracetamol",
                1,
                "1.0",
                "tablet(s)",
                com.example.medicinetime.data.alarmId
            )
        }
    }

    @VisibleForTesting
    fun addMedicines(vararg medicineAlarms: MedicineAlarm?) {
        for (medicineAlarm in medicineAlarms) {
            MEDICINE_SERVICE_DATA!![java.lang.String.valueOf(medicineAlarm.getId())] = medicineAlarm
        }
    }

    fun addMedicine(vararg medicineAlarms: MedicineAlarm?) {
        for (medicineAlarm in medicineAlarms) {
            MEDICINE_SERVICE_DATA!![java.lang.String.valueOf(medicineAlarm.getId())] = medicineAlarm
        }
    }

    fun getMedicineHistory(loadHistoryCallbacks: LoadHistoryCallbacks) {
        loadHistoryCallbacks.onHistoryLoaded(ArrayList<History>(HISTORY_SERVICE_DATA!!.values))
    }

    fun getMedicineAlarmById(id: Long, callback: GetTaskCallback) {
        callback.onTaskLoaded(MEDICINE_SERVICE_DATA!![id.toString()])
    }

    fun saveMedicine(medicineAlarm: MedicineAlarm, pills: Pills) {
        medicineAlarm.addId(pills.getPillId())
        MEDICINE_SERVICE_DATA!![java.lang.String.valueOf(pills.getPillId())] =
            medicineAlarm
    }

    fun getMedicineListByDay(day: Int, callbacks: LoadMedicineCallbacks) {
        callbacks.onMedicineLoaded(ArrayList<Any?>(MEDICINE_SERVICE_DATA!!.values))
    }

    fun medicineExits(pillName: String?): Boolean {
        return false
    }

    fun tempIds(): List<Long>? {
        return null
    }

    fun deleteAlarm(alarmId: Long) {
        MEDICINE_SERVICE_DATA!!.remove(alarmId.toString())
    }

    fun getMedicineByPillName(pillName: String?): List<MedicineAlarm> {
        val medicineAlarms: MutableList<MedicineAlarm> = ArrayList<MedicineAlarm>()
        for ((_, medicineAlarm) in MEDICINE_SERVICE_DATA!!) {
            if (medicineAlarm.getPillName().equalsIgnoreCase(pillName)) {
                medicineAlarms.add(medicineAlarm)
            }
        }
        return medicineAlarms
    }

    fun getAllAlarms(pillName: String?): List<MedicineAlarm> {
        val medicineAlarms: MutableList<MedicineAlarm> = ArrayList<MedicineAlarm>()
        for ((_, medicineAlarm) in MEDICINE_SERVICE_DATA!!) {
            if (medicineAlarm.getPillName().equalsIgnoreCase(pillName)) {
                medicineAlarms.add(medicineAlarm)
            }
        }
        return medicineAlarms
    }

    fun getPillsByName(pillName: String?): Pills? {
        for ((_, pills) in PILLS_SERVICE_DATA!!) {
            if (pills.getPillName().equalsIgnoreCase(pillName)) {
                return pills
            }
        }
        return null
    }

    fun savePills(pills: Pills): Long {
        PILLS_SERVICE_DATA!![java.lang.String.valueOf(
            pills.getPillId()
        )] = pills
        return pills.getPillId()
    }

    fun saveToHistory(history: History) {
        HISTORY_SERVICE_DATA!![java.lang.String.valueOf(history.getPillName())] =
            history
    }
}