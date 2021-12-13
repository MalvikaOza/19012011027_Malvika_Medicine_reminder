package com.example.medicinetime.medicine

import android.content.Context
import android.util.Log
import com.example.medicinetime.addmedicine.AddMedicineActivity
import java.util.*


class MedicinePresenter internal constructor(
    medicineRepository: MedicineRepository,
    medView: MedicineContract.View
) : MedicineContract.Presenter {
    private val mMedicineRepository: MedicineRepository
    private val mMedView: MedicineContract.View
    override fun loadMedicinesByDay(day: Int, showIndicator: Boolean) {
        loadListByDay(day, showIndicator)
    }

    fun deleteMedicineAlarm(medicineAlarm: MedicineAlarm, context: Context) {
        val alarms: List<MedicineAlarm> =
            mMedicineRepository.getAllAlarms(medicineAlarm.getPillName())
        for (alarm in alarms) {
            mMedicineRepository.deleteAlarm(alarm.getId())
            /** This intent invokes the activity ReminderActivity, which in turn opens the AlertAlarm window  */
            val intent = Intent(context, ReminderActivity::class.java)
            intent.putExtra(ReminderFragment.EXTRA_ID, alarm.getAlarmId())
            val operation: PendingIntent = PendingIntent.getActivity(
                context,
                alarm.getAlarmId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            /** Getting a reference to the System Service ALARM_SERVICE  */
            val alarmManager: AlarmManager = Objects.requireNonNull(context).getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager
            if (alarmManager != null) {
                alarmManager.cancel(operation)
            }
        }
        mMedView.showMedicineDeletedSuccessfully()
    }

    override fun start() {}
    override fun onStart(day: Int) {
        Log.d("TAG", "onStart: $day")
        loadMedicinesByDay(day, true)
    }

    override fun reload(day: Int) {
        Log.d("TAG", "reload: $day")
        loadListByDay(day, true)
    }

    override fun result(requestCode: Int, resultCode: Int) {
        if (AddMedicineActivity.REQUEST_ADD_TASK === requestCode && Activity.RESULT_OK == resultCode) {
            mMedView.showSuccessfullySavedMessage()
        }
    }

    override fun addNewMedicine() {
        mMedView.showAddMedicine()
    }

    private fun loadListByDay(day: Int, showLoadingUi: Boolean) {
        if (showLoadingUi) mMedView.showLoadingIndicator(true)
        mMedicineRepository.getMedicineListByDay(day, object : LoadMedicineCallbacks() {
            fun onMedicineLoaded(medicineAlarmList: List<MedicineAlarm>) {
                processMedicineList(medicineAlarmList)
                // The view may not be able to handle UI updates anymore
                if (!mMedView.isActive()) {
                    return
                }
                if (showLoadingUi) {
                    mMedView.showLoadingIndicator(false)
                }
            }

            fun onDataNotAvailable() {
                if (!mMedView.isActive()) {
                    return
                }
                if (showLoadingUi) {
                    mMedView.showLoadingIndicator(false)
                }
                mMedView.showNoMedicine()
            }
        })
    }

    private fun processMedicineList(medicineAlarmList: List<MedicineAlarm>) {
        if (medicineAlarmList.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            mMedView.showNoMedicine()
        } else {
            //Show the list of Medicines
            Collections.sort(medicineAlarmList)
            mMedView.showMedicineList(medicineAlarmList)
        }
    }

    init {
        mMedicineRepository = medicineRepository
        mMedView = medView
        medView.setPresenter(this)
    }
}