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
import java.util.*


class History {
    var hourTaken = 0
    var minuteTaken = 0
    var dateString: String? = null
    var pillName: String? = null
    var action = 0
    var doseQuantity: String? = null
    var doseUnit: String? = null
    var alarmId = 0

    constructor() {}
    constructor(
        hourTaken: Int,
        minuteTaken: Int,
        dateString: String?,
        pillName: String?,
        action: Int,
        doseQuantity: String?,
        doseUnit: String?,
        alarmId: Int
    ) {
        this.hourTaken = hourTaken
        this.minuteTaken = minuteTaken
        this.dateString = dateString
        this.pillName = pillName
        this.action = action
        this.doseQuantity = doseQuantity
        this.doseUnit = doseUnit
        this.alarmId = alarmId
    }

    val am_pmTaken: String
        get() = if (hourTaken < 12) "am" else "pm"

    /**
     * A helper method which returns the time of the alarm in string form
     * hour:minutes am/pm
     */
    val stringTime: String
        get() {
            var nonMilitaryHour = hourTaken % 12
            if (nonMilitaryHour == 0) nonMilitaryHour = 12
            var min = Integer.toString(minuteTaken)
            if (minuteTaken < 10) min = "0$minuteTaken"
            return String.format(Locale.getDefault(), "%d:%s %s", nonMilitaryHour, min, am_pmTaken)
        }
    val formattedDate: String
        get() = String.format(Locale.getDefault(), "%s %s", dateString, stringTime)

    /**
     * A helper method which returns the formatted medicine dose
     * doseQuantity doseUnit
     */
    val formattedDose: String
        get() = String.format(Locale.getDefault(), "%s %s", doseQuantity, doseUnit)
}