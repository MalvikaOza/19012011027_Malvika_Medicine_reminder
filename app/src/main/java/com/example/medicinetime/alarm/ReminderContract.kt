package com.example.medicinetime.alarm

import com.example.medicinetime.BasePresenter


interface ReminderContract {
    interface View : BaseView<Presenter?> {
        fun showMedicine(medicineAlarm: MedicineAlarm?)
        fun showNoData()
        val isActive: Boolean
        fun onFinish()
    }

    interface Presenter : BasePresenter {
        fun finishActivity()
        fun onStart(id: Long)
        fun loadMedicineById(id: Long)
        fun addPillsToHistory(history: History?)
    }
}