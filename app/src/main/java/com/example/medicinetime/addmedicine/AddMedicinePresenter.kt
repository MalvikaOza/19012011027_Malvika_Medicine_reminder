package com.example.medicinetime.addmedicine

import com.example.medicinetime.data.source.MedicineAlarm


class AddMedicinePresenter(
    private val mMedicineId: Int,
    mMedicineRepository: MedicineDataSource,
    mAddMedicineView: AddMedicineContract.View?,
    mIsDataMissing: Boolean
) : AddMedicineContract.Presenter, MedicineDataSource.GetTaskCallback {
    private val mMedicineRepository: MedicineDataSource
    private val mAddMedicineView: AddMedicineContract.View?
    override val isDataMissing: Boolean
    private val isNewTask: Boolean
        private get() = mMedicineId <= 0

    override fun start() {}
    override fun saveMedicine(alarm: MedicineAlarm?, pills: Pills?) {
        mMedicineRepository.saveMedicine(alarm, pills)
    }

    override fun isMedicineExits(pillName: String?): Boolean {
        return mMedicineRepository.medicineExits(pillName)
    }

    override fun addPills(pills: Pills?): Long {
        return mMedicineRepository.savePills(pills)
    }

    override fun getPillsByName(pillName: String?): Pills {
        return mMedicineRepository.getPillsByName(pillName)
    }

    override fun getMedicineByPillName(pillName: String?): List<MedicineAlarm> {
        return mMedicineRepository.getMedicineByPillName(pillName)
    }

    override fun tempIds(): List<Long> {
        return mMedicineRepository.tempIds()
    }

    override fun deleteMedicineAlarm(alarmId: Long) {
        mMedicineRepository.deleteAlarm(alarmId)
    }

    fun onTaskLoaded(medicineAlarm: MedicineAlarm?) {
        // The view may not be able to handle UI updates anymore
        /*if (mAddMedicineView.isActive()){
            mAddMedicineView.setDose(medicineAlarm.getDose());
            mAddMedicineView.setMedName(medicineAlarm.getMedicineName());
            mAddMedicineView.setDays(medicineAlarm.getDays());
            mAddMedicineView.setTime(medicineAlarm.getTime());
        }
        mIsDataMissing = false;*/
    }

    fun onDataNotAvailable() {
        if (mAddMedicineView!!.isActive()) {
            mAddMedicineView!!.showEmptyMedicineError()
        }
    }

    init {
        this.mMedicineRepository = mMedicineRepository
        this.mAddMedicineView = mAddMedicineView
        isDataMissing = mIsDataMissing
        mAddMedicineView.setPresenter(this)
    }
}