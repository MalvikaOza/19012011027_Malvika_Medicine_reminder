package com.example.medicinetime

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
import android.app.*
import com.google.android.material.snackbar.Snackbar
import android.os.PersistableBundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import android.widget.CheckBox
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.widget.TimePicker
import android.widget.ArrayAdapter
import butterknife.OnItemSelected


class MedicineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (instance == null) {
            instance = applicationContext
        }
    }

    companion object {
        var instance: Context? = null
            private set
    }
}