package com.example.medicinetime.addmedicine

import com.example.medicinetime.BasePresenter

/**
 * Created by gautam on 12/07/17.
 */
interface AddMedicineContract {
    interface View : BaseView<Presenter?> {
        fun showEmptyMedicineError()
        fun showMedicineList()
        val isActive: Boolean
    }

    interface Presenter : BasePresenter {
        fun saveMedicine(alarm: MedicineAlarm?, pills: Pills?)
        val isDataMissing: Boolean
        fun isMedicineExits(pillName: String?): Boolean
        fun addPills(pills: Pills?): Long
        fun getPillsByName(pillName: String?): Pills
        fun getMedicineByPillName(pillName: String?): List<MedicineAlarm>
        fun tempIds(): List<Long?>?
        fun deleteMedicineAlarm(alarmId: Long)
    }
}