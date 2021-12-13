package com.example.medicinetime.alarm

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.medicinetime.data.source.History
import java.text.SimpleDateFormat
import java.util.*


class ReminderFragment : Fragment(), ReminderContract.View {
    @BindView(R.id.tv_med_time)
    var tvMedTime: RobotoBoldTextView? = null

    @BindView(R.id.tv_medicine_name)
    var tvMedicineName: RobotoBoldTextView? = null

    @BindView(R.id.tv_dose_details)
    var tvDoseDetails: RobotoRegularTextView? = null

    @BindView(R.id.iv_ignore_med)
    var ivIgnoreMed: ImageView? = null

    @BindView(R.id.iv_take_med)
    var ivTakeMed: ImageView? = null

    @BindView(R.id.linearLayout)
    var linearLayout: LinearLayout? = null
    var unbinder: Unbinder? = null
    private var medicineAlarm: MedicineAlarm? = null
    private var id: Long = 0
    private var mMediaPlayer: MediaPlayer? = null
    private var mVibrator: Vibrator? = null
    private var presenter: ReminderContract.Presenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments!!.getLong(EXTRA_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_reminder, container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    fun setPresenter(presenter: ReminderContract.Presenter?) {
        this.presenter = presenter
    }

    override fun showMedicine(medicineAlarm: MedicineAlarm) {
        this.medicineAlarm = medicineAlarm
        mVibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 1000, 10000)
        mVibrator.vibrate(pattern, 0)
        mMediaPlayer = MediaPlayer.create(context, R.raw.cuco_sound)
        mMediaPlayer.setLooping(true)
        mMediaPlayer.start()
        tvMedTime.setText(medicineAlarm.getStringTime())
        tvMedicineName.setText(medicineAlarm.getPillName())
        tvDoseDetails.setText(medicineAlarm.getFormattedDose())
    }

    override fun showNoData() {
        //
    }

    override fun onResume() {
        super.onResume()
        presenter!!.onStart(id)
    }

    @OnClick(R.id.iv_take_med)
    fun onMedTakeClick() {
        onMedicineTaken()
        stopMedialPlayer()
        stopVibrator()
    }

    @OnClick(R.id.iv_ignore_med)
    fun onMedIgnoreClick() {
        onMedicineIgnored()
        stopMedialPlayer()
        stopVibrator()
    }

    private fun stopMedialPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop()
            mMediaPlayer.release()
        }
    }

    private fun stopVibrator() {
        if (mVibrator != null) {
            mVibrator.cancel()
        }
    }

    private fun onMedicineTaken() {
        val history = History()
        val takeTime = Calendar.getInstance()
        val date = takeTime.time
        val dateString = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
        val hour = takeTime[Calendar.HOUR_OF_DAY]
        val minute = takeTime[Calendar.MINUTE]
        val am_pm = if (hour < 12) "am" else "pm"
        history.setHourTaken(hour)
        history.setMinuteTaken(minute)
        history.setDateString(dateString)
        history.setPillName(medicineAlarm.getPillName())
        history.setAction(1)
        history.setDoseQuantity(medicineAlarm.getDoseQuantity())
        history.setDoseUnit(medicineAlarm.getDoseUnit())
        presenter!!.addPillsToHistory(history)
        val stringMinute: String
        stringMinute = if (minute < 10) "0$minute" else "" + minute
        var nonMilitaryHour = hour % 12
        if (nonMilitaryHour == 0) nonMilitaryHour = 12
        Toast.makeText(
            context,
            medicineAlarm.getPillName()
                .toString() + " was taken at " + nonMilitaryHour + ":" + stringMinute + " " + am_pm + ".",
            Toast.LENGTH_SHORT
        ).show()
        val returnHistory = Intent(context, MedicineActivity::class.java)
        startActivity(returnHistory)
        activity!!.finish()
    }

    private fun onMedicineIgnored() {
        val history = History()
        val takeTime = Calendar.getInstance()
        val date = takeTime.time
        val dateString = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
        val hour = takeTime[Calendar.HOUR_OF_DAY]
        val minute = takeTime[Calendar.MINUTE]
        val am_pm = if (hour < 12) "am" else "pm"
        history.setHourTaken(hour)
        history.setMinuteTaken(minute)
        history.setDateString(dateString)
        history.setPillName(medicineAlarm.getPillName())
        history.setAction(2)
        history.setDoseQuantity(medicineAlarm.getDoseQuantity())
        history.setDoseUnit(medicineAlarm.getDoseUnit())
        presenter!!.addPillsToHistory(history)
        val stringMinute: String
        stringMinute = if (minute < 10) "0$minute" else "" + minute
        var nonMilitaryHour = hour % 12
        if (nonMilitaryHour == 0) nonMilitaryHour = 12
        Toast.makeText(
            context,
            medicineAlarm.getPillName()
                .toString() + " was ignored at " + nonMilitaryHour + ":" + stringMinute + " " + am_pm + ".",
            Toast.LENGTH_SHORT
        ).show()
        val returnHistory = Intent(context, MedicineActivity::class.java)
        startActivity(returnHistory)
        activity!!.finish()
    }

    override val isActive: Boolean
        get() = isAdded

    override fun onFinish() {
        stopMedialPlayer()
        stopVibrator()
        activity!!.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        fun newInstance(id: Long): ReminderFragment {
            val args = Bundle()
            args.putLong(EXTRA_ID, id)
            val fragment = ReminderFragment()
            fragment.arguments = args
            return fragment
        }
    }
}