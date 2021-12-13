package com.example.medicinetime.report

import com.example.medicinetime.data.source.History
import java.util.ArrayList


class MonthlyReportPresenter(
    medicineRepository: MedicineRepository,
    monthlyReportView: MonthlyReportContract.View?
) : MonthlyReportContract.Presenter {
    private val mMedicineRepository: MedicineRepository
    private val mMonthlyReportView: MonthlyReportContract.View?
    private var mCurrentFilteringType: FilterType = FilterType.ALL_MEDICINES
    override fun start() {
        loadHistory(true)
    }

    override fun loadHistory(showLoading: Boolean) {
        loadHistoryFromDb(showLoading)
    }

    private fun loadHistoryFromDb(showLoading: Boolean) {
        if (showLoading) {
            mMonthlyReportView!!.setLoadingIndicator(true)
        }
        mMedicineRepository.getMedicineHistory(object : LoadHistoryCallbacks() {
            fun onHistoryLoaded(historyList: List<History?>) {
                val historyShowList: MutableList<History> = ArrayList<History>()

                //We will filter the History based on request type
                for (history in historyList) {
                    when (mCurrentFilteringType) {
                        ALL_MEDICINES -> historyShowList.add(history)
                        TAKEN_MEDICINES -> if (history.getAction() === 1) {
                            historyShowList.add(history)
                        }
                        IGNORED_MEDICINES -> if (history.getAction() === 2) {
                            historyShowList.add(history)
                        }
                    }
                }
                processHistory(historyShowList)
                if (!mMonthlyReportView!!.isActive()) {
                    return
                }
                if (showLoading) {
                    mMonthlyReportView!!.setLoadingIndicator(false)
                }
            }

            fun onDataNotAvailable() {
                if (!mMonthlyReportView!!.isActive()) {
                    return
                }
                if (showLoading) {
                    mMonthlyReportView!!.setLoadingIndicator(false)
                }
                mMonthlyReportView!!.showLoadingError()
            }
        })
    }

    private fun processHistory(historyList: List<History>) {
        if (historyList.isEmpty()) {
            // Show a message indicating there are no history for that filter type.
            processEmptyHistory()
        } else {
            //Show the list of history
            mMonthlyReportView!!.showHistoryList(historyList)
            //Set filter label's text
            showFilterLabel()
        }
    }

    private fun showFilterLabel() {
        when (mCurrentFilteringType) {
            ALL_MEDICINES -> mMonthlyReportView!!.showAllFilterLabel()
            TAKEN_MEDICINES -> mMonthlyReportView!!.showTakenFilterLabel()
            IGNORED_MEDICINES -> mMonthlyReportView!!.showIgnoredFilterLabel()
            else -> mMonthlyReportView!!.showAllFilterLabel()
        }
    }

    private fun processEmptyHistory() {
        when (mCurrentFilteringType) {
            ALL_MEDICINES -> mMonthlyReportView!!.showNoHistory()
            TAKEN_MEDICINES -> mMonthlyReportView!!.showNoTakenHistory()
            IGNORED_MEDICINES -> mMonthlyReportView!!.showNoIgnoredHistory()
            else -> mMonthlyReportView!!.showNoHistory()
        }
    }

    fun setFiltering(filterType: FilterType) {
        mCurrentFilteringType = filterType
    }

    override val filterType: com.example.medicinetime.report.FilterType?
        get() = mCurrentFilteringType

    init {
        mMedicineRepository = medicineRepository
        mMonthlyReportView = monthlyReportView
        mMonthlyReportView.setPresenter(this)
    }
}