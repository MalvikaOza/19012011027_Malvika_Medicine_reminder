package com.example.medicinetime.alarm

import com.example.medicinetime.data.source.History


class ReminderPresenter internal constructor(
    medicineRepository: MedicineRepository,
    reminderView: ReminderContract.View
) : ReminderContract.Presenter {
    private val mMedicineRepository: MedicineRepository
    private val mReminderView: ReminderContract.View
    override fun start() {}
    override fun finishActivity() {
        mReminderView.onFinish()
    }

    override fun onStart(id: Long) {
        loadMedicineById(id)
    }

    override fun loadMedicineById(id: Long) {
        loadMedicine(id)
    }

    private fun loadMedicine(id: Long) {
        mMedicineRepository.getMedicineAlarmById(id, object : GetTaskCallback() {
            fun onTaskLoaded(medicineAlarm: MedicineAlarm?) {
                if (!mReminderView.isActive()) {
                    return
                }
                if (medicineAlarm == null) {
                    return
                }
                mReminderView.showMedicine(medicineAlarm)
            }

            fun onDataNotAvailable() {
                mReminderView.showNoData()
            }
        })
    }

    override fun addPillsToHistory(history: History?) {
        mMedicineRepository.saveToHistory(history)
    }

    init {
        mMedicineRepository = medicineRepository
        mReminderView = reminderView
        mReminderView.setPresenter(this)
    }
}