package com.example.medicinetime.utils

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
import butterknife.OnClick
import android.widget.Toast
import android.graphics.Typeface
import androidx.annotation.DrawableRes
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
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
import android.content.Context
import android.view.*
import android.widget.TimePicker
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import butterknife.OnItemSelected


object ActivityUtils {
    /**
     * The `fragment` is added to the container view with id `frameId`. The operation is
     * performed by the `fragmentManager`.
     */
    fun addFragmentToActivity(
        fragmentManager: FragmentManager,
        fragment: Fragment, frameId: Int
    ) {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commit()
    }

    fun setBackGround(context: Context?, view: View, @DrawableRes drawable: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.background = ContextCompat.getDrawable(context!!, drawable)
        } else {
            view.setBackgroundDrawable(ContextCompat.getDrawable(context!!, drawable))
        }
    }
}