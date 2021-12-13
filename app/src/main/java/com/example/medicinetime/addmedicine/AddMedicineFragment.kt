package com.example.medicinetime.addmedicine

import android.R
import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.medicinetime.alarm.ReminderActivity
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class AddMedicineFragment : Fragment(), AddMedicineContract.View {
    @BindView(R.id.edit_med_name)
    var editMedName: EditText? = null

    @BindView(R.id.every_day)
    var everyDay: AppCompatCheckBox? = null

    @BindView(R.id.dv_sunday)
    var dvSunday: DayViewCheckBox? = null

    @BindView(R.id.dv_monday)
    var dvMonday: DayViewCheckBox? = null

    @BindView(R.id.dv_tuesday)
    var dvTuesday: DayViewCheckBox? = null

    @BindView(R.id.dv_wednesday)
    var dvWednesday: DayViewCheckBox? = null

    @BindView(R.id.dv_thursday)
    var dvThursday: DayViewCheckBox? = null

    @BindView(R.id.dv_friday)
    var dvFriday: DayViewCheckBox? = null

    @BindView(R.id.dv_saturday)
    var dvSaturday: DayViewCheckBox? = null

    @BindView(R.id.checkbox_layout)
    var checkboxLayout: LinearLayout? = null

    @BindView(R.id.tv_medicine_time)
    var tvMedicineTime: RobotoBoldTextView? = null

    @BindView(R.id.tv_dose_quantity)
    var tvDoseQuantity: EditText? = null

    @BindView(R.id.spinner_dose_units)
    var spinnerDoseUnits: AppCompatSpinner? = null
    private var doseUnitList: List<String>? = null
    private val dayOfWeekList = BooleanArray(7)
    private var hour = 0
    private var minute = 0
    var unbinder: Unbinder? = null
    private var mPresenter: AddMedicineContract.Presenter? = null
    private var rootView: View? = null
    private var doseUnit: String? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fab: FloatingActionButton =
            Objects.requireNonNull(activity).findViewById(R.id.fab_edit_task_done)
        fab.setImageResource(R.drawable.ic_done)
        fab.setOnClickListener(setClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_add_medicine, container, false)
        unbinder = ButterKnife.bind(this, rootView)
        setCurrentTime()
        setSpinnerDoseUnits()
        return rootView
    }

    fun setPresenter(presenter: AddMedicineContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showEmptyMedicineError() {
        // Snackbar.make(mTitle, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show();
    }

    override fun showMedicineList() {
        Objects.requireNonNull(activity).setResult(Activity.RESULT_OK)
        activity!!.finish()
    }

    override val isActive: Boolean
        get() = isAdded

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    @OnClick(
        R.id.every_day,
        R.id.dv_monday,
        R.id.dv_tuesday,
        R.id.dv_wednesday,
        R.id.dv_thursday,
        R.id.dv_friday,
        R.id.dv_saturday,
        R.id.dv_sunday
    )
    fun onCheckboxClicked(view: View) {
        val checked: Boolean = (view as CheckBox).isChecked()
        when (view.id) {
            R.id.dv_monday -> if (checked) {
                dayOfWeekList[1] = true
            } else {
                dayOfWeekList[1] = false
                everyDay.setChecked(false)
            }
            R.id.dv_tuesday -> if (checked) {
                dayOfWeekList[2] = true
            } else {
                dayOfWeekList[2] = false
                everyDay.setChecked(false)
            }
            R.id.dv_wednesday -> if (checked) {
                dayOfWeekList[3] = true
            } else {
                dayOfWeekList[3] = false
                everyDay.setChecked(false)
            }
            R.id.dv_thursday -> if (checked) {
                dayOfWeekList[4] = true
            } else {
                dayOfWeekList[4] = false
                everyDay.setChecked(false)
            }
            R.id.dv_friday -> if (checked) {
                dayOfWeekList[5] = true
            } else {
                dayOfWeekList[5] = false
                everyDay.setChecked(false)
            }
            R.id.dv_saturday -> if (checked) {
                dayOfWeekList[6] = true
            } else {
                dayOfWeekList[6] = false
                everyDay.setChecked(false)
            }
            R.id.dv_sunday -> if (checked) {
                dayOfWeekList[0] = true
            } else {
                dayOfWeekList[0] = false
                everyDay.setChecked(false)
            }
            R.id.every_day -> {
                val ll: LinearLayout =
                    rootView!!.findViewById<View>(R.id.checkbox_layout) as LinearLayout
                var i = 0
                while (i < ll.getChildCount()) {
                    val v: View = ll.getChildAt(i)
                    (v as DayViewCheckBox).setChecked(checked)
                    onCheckboxClicked(v)
                    i++
                }
            }
        }
    }

    @OnClick(R.id.tv_medicine_time)
    fun onMedicineTimeClick() {
        showTimePicker()
    }

    private fun showTimePicker() {
        val mCurrentTime = Calendar.getInstance()
        hour = mCurrentTime[Calendar.HOUR_OF_DAY]
        minute = mCurrentTime[Calendar.MINUTE]
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(context, object : OnTimeSetListener {
            override fun onTimeSet(timePicker: TimePicker, selectedHour: Int, selectedMinute: Int) {
                hour = selectedHour
                minute = selectedMinute
                tvMedicineTime.setText(
                    String.format(
                        Locale.getDefault(),
                        "%d:%d",
                        selectedHour,
                        selectedMinute
                    )
                )
            }
        }, hour, minute, false) //No 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    private fun setCurrentTime() {
        val mCurrentTime = Calendar.getInstance()
        hour = mCurrentTime[Calendar.HOUR_OF_DAY]
        minute = mCurrentTime[Calendar.MINUTE]
        tvMedicineTime.setText(String.format(Locale.getDefault(), "%d:%d", hour, minute))
    }

    private fun setSpinnerDoseUnits() {
        doseUnitList = Arrays.asList(*resources.getStringArray(R.array.medications_shape_array))
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            Objects.requireNonNull(
                context
            ), R.layout.simple_dropdown_item_1line, doseUnitList
        )
        spinnerDoseUnits.setAdapter(adapter)
    }

    @OnItemSelected(R.id.spinner_dose_units)
    fun onSpinnerItemSelected(position: Int) {
        if (doseUnitList == null || doseUnitList!!.isEmpty()) {
            return
        }
        doseUnit = doseUnitList!![position]
        Log.d("TAG", doseUnit)
    }

    private val setClickListener = View.OnClickListener {
        var checkBoxCounter = 0
        val pill_name: String = editMedName.getText().toString()
        val doseQuantity: String = tvDoseQuantity.getText().toString()
        val takeTime = Calendar.getInstance()
        val date = takeTime.time
        val dateString = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
        /** Updating model  */
        /** Updating model  */
        val alarm = MedicineAlarm()
        val alarmId = Random().nextInt(100)
        /** If Pill does not already exist  */
        /** If Pill does not already exist  */
        if (!mPresenter!!.isMedicineExits(pill_name)) {
            val pill = Pills()
            pill.setPillName(pill_name)
            alarm.setDateString(dateString)
            alarm.setHour(hour)
            alarm.setMinute(minute)
            alarm.setPillName(pill_name)
            alarm.setDayOfWeek(dayOfWeekList)
            alarm.setDoseUnit(doseUnit)
            alarm.setDoseQuantity(doseQuantity)
            alarm.setAlarmId(alarmId)
            pill.addAlarm(alarm)
            val pillId: Long = mPresenter!!.addPills(pill)
            pill.setPillId(pillId)
            mPresenter!!.saveMedicine(alarm, pill)
        } else { // If Pill already exists
            val pill: Pills = mPresenter!!.getPillsByName(pill_name)
            alarm.setDateString(dateString)
            alarm.setHour(hour)
            alarm.setMinute(minute)
            alarm.setPillName(pill_name)
            alarm.setDayOfWeek(dayOfWeekList)
            alarm.setDoseUnit(doseUnit)
            alarm.setDoseQuantity(doseQuantity)
            alarm.setAlarmId(alarmId)
            pill.addAlarm(alarm)
            mPresenter!!.saveMedicine(alarm, pill)
        }
        var ids: List<Long> = LinkedList()
        try {
            val alarms: List<MedicineAlarm> = mPresenter!!.getMedicineByPillName(pill_name)
            for (tempAlarm in alarms) {
                if (tempAlarm.getHour() === hour && tempAlarm.getMinute() === minute) {
                    ids = tempAlarm.getIds()
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for (i in 0..6) {
            if (dayOfWeekList[i] && pill_name.length != 0) {
                val dayOfWeek = i + 1
                val _id = ids[checkBoxCounter]
                val id = _id.toInt()
                checkBoxCounter++
                /** This intent invokes the activity ReminderActivity, which in turn opens the AlertAlarm window  */
                /** This intent invokes the activity ReminderActivity, which in turn opens the AlertAlarm window  */
                val intent = Intent(activity, ReminderActivity::class.java)
                intent.putExtra(ReminderFragment.EXTRA_ID, _id)
                val operation: PendingIntent = PendingIntent.getActivity(
                    activity,
                    id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                /** Getting a reference to the System Service ALARM_SERVICE  */
                /** Getting a reference to the System Service ALARM_SERVICE  */
                val alarmManager: AlarmManager = Objects.requireNonNull(activity)
                    .getSystemService(Context.ALARM_SERVICE) as AlarmManager
                /** Creating a calendar object corresponding to the date and time set by the user  */
                /** Creating a calendar object corresponding to the date and time set by the user  */
                val calendar = Calendar.getInstance()
                calendar[Calendar.HOUR_OF_DAY] = hour
                calendar[Calendar.MINUTE] = minute
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
                calendar[Calendar.DAY_OF_WEEK] = dayOfWeek
                /** Converting the date and time in to milliseconds elapsed since epoch  */
                /** Converting the date and time in to milliseconds elapsed since epoch  */
                var alarm_time = calendar.timeInMillis
                if (calendar.before(Calendar.getInstance())) alarm_time += AlarmManager.INTERVAL_DAY * 7
                assert(alarmManager != null)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, alarm_time,
                    AlarmManager.INTERVAL_DAY * 7, operation
                )
            }
        }
        Toast.makeText(context, "Alarm for $pill_name is set successfully", Toast.LENGTH_SHORT)
            .show()
        showMedicineList()
    }

    companion object {
        const val ARGUMENT_EDIT_MEDICINE_ID = "ARGUMENT_EDIT_MEDICINE_ID"
        const val ARGUMENT_EDIT_MEDICINE_NAME = "ARGUMENT_EDIT_MEDICINE_NAME"
        fun newInstance(): AddMedicineFragment {
            val args = Bundle()
            val fragment = AddMedicineFragment()
            fragment.arguments = args
            return fragment
        }
    }
}