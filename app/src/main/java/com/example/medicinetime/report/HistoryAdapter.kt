package com.example.medicinetime.report

import android.view.View
import android.widget.ImageView
import com.example.medicinetime.data.source.History


class HistoryAdapter internal constructor(historyList: List<History>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder?>() {
    private var mHistoryList: List<History>? = null
    fun replaceData(tasks: List<History>) {
        setList(tasks)
        notifyDataSetChanged()
    }

    private fun setList(historyList: List<History>) {
        mHistoryList = historyList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view: View =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history: History = mHistoryList!![position] ?: return
        holder.tvMedDate.setText(history.getFormattedDate())
        setMedicineAction(holder, history.getAction())
        holder.tvMedicineName.setText(history.getPillName())
        holder.tvDoseDetails.setText(history.getFormattedDose())
    }

    private fun setMedicineAction(holder: HistoryViewHolder, action: Int) {
        when (action) {
            0 -> holder.ivMedicineAction!!.visibility = View.GONE
            1 -> {
                holder.ivMedicineAction!!.visibility = View.VISIBLE
                holder.ivMedicineAction!!.setImageResource(R.drawable.image_reminder_taken)
            }
            2 -> {
                holder.ivMedicineAction!!.setImageResource(R.drawable.image_reminder_not_taken)
                holder.ivMedicineAction!!.visibility = View.VISIBLE
            }
            else -> holder.ivMedicineAction!!.visibility = View.GONE
        }
    }

    val itemCount: Int
        get() = if (mHistoryList != null && !mHistoryList!!.isEmpty()) mHistoryList!!.size else 0

    class HistoryViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_med_date)
        var tvMedDate: RobotoBoldTextView? = null

        @BindView(R.id.tv_medicine_name)
        var tvMedicineName: RobotoBoldTextView? = null

        @BindView(R.id.tv_dose_details)
        var tvDoseDetails: RobotoRegularTextView? = null

        @BindView(R.id.iv_medicine_action)
        var ivMedicineAction: ImageView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    init {
        setList(historyList)
    }
}