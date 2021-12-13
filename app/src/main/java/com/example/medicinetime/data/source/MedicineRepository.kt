package com.example.medicinetime.data.source

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import kotlin.Throws
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import com.example.medicinetime.R
import android.os.Bundle
import butterknife.ButterKnife
import android.content.Intent
import android.widget.LinearLayout
import butterknife.Unbinder
import android.media.MediaPlayer
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.ViewGroup
import butterknife.OnClick
import android.widget.Toast
import android.graphics.Typeface
import androidx.annotation.DrawableRes
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.MenuInflater
import com.example.medicinetime.medicine.MedicineAdapter.OnItemClickListener
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import android.widget.RelativeLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.widget.FrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import androidx.core.view.ViewCompat
import MedicineAdapter.OnItemClickListener
import com.google.android.material.snackbar.Snackbar
import android.app.PendingIntent
import android.app.AlarmManager
import android.app.Activity
import android.os.PersistableBundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import android.widget.CheckBox
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.widget.TimePicker
import android.widget.ArrayAdapter
import butterknife.OnItemSelected


class MedicineRepository private constructor(localDataSource: MedicineDataSource) :
    MedicineDataSource {
    private val localDataSource: MedicineDataSource
    override fun getMedicineHistory(loadHistoryCallbacks: LoadHistoryCallbacks) {
        localDataSource.getMedicineHistory(object : LoadHistoryCallbacks() {
            fun onHistoryLoaded(historyList: List<History?>?) {
                loadHistoryCallbacks.onHistoryLoaded(historyList)
            }

            fun onDataNotAvailable() {
                loadHistoryCallbacks.onDataNotAvailable()
            }
        })
    }

    override fun getMedicineAlarmById(id: Long, callback: GetTaskCallback) {
        localDataSource.getMedicineAlarmById(id, object : GetTaskCallback() {
            fun onTaskLoaded(medicineAlarm: MedicineAlarm?) {
                if (medicineAlarm == null) {
                    return
                }
                callback.onTaskLoaded(medicineAlarm)
            }

            fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun saveMedicine(medicineAlarm: MedicineAlarm?, pills: Pills?) {
        localDataSource.saveMedicine(medicineAlarm, pills)
    }

    override fun getMedicineListByDay(day: Int, callbacks: LoadMedicineCallbacks) {
        localDataSource.getMedicineListByDay(day, object : LoadMedicineCallbacks() {
            fun onMedicineLoaded(medicineAlarmList: List<MedicineAlarm?>?) {
                callbacks.onMedicineLoaded(medicineAlarmList)
            }

            fun onDataNotAvailable() {
                callbacks.onDataNotAvailable()
            }
        })
    }

    override fun medicineExits(pillName: String?): Boolean {
        return false
    }

    override fun tempIds(): List<Long> {
        return localDataSource.tempIds()
    }

    override fun deleteAlarm(alarmId: Long) {
        localDataSource.deleteAlarm(alarmId)
    }

    override fun getMedicineByPillName(pillName: String?): List<MedicineAlarm> {
        return localDataSource.getMedicineByPillName(pillName)
    }

    override fun getAllAlarms(pillName: String?): List<MedicineAlarm> {
        return localDataSource.getAllAlarms(pillName)
    }

    override fun getPillsByName(pillName: String?): Pills {
        return localDataSource.getPillsByName(pillName)
    }

    override fun savePills(pills: Pills?): Long {
        return localDataSource.savePills(pills)
    }

    override fun saveToHistory(history: History?) {
        localDataSource.saveToHistory(history)
    }

    companion object {
        private var mInstance: MedicineRepository? = null
        fun getInstance(localDataSource: MedicineDataSource): MedicineRepository? {
            if (mInstance == null) {
                mInstance = MedicineRepository(localDataSource)
            }
            return mInstance
        }
    }

    init {
        this.localDataSource = localDataSource
    }
}