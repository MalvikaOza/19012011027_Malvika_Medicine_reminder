package com.example.medicinetime.medicine

import android.view.View
import android.widget.ImageView
import com.example.medicinetime.data.source.MedicineAlarm


class MedicineAdapter(medicineAlarmList: List<MedicineAlarm>?) :
    RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder?>() {
    private var medicineAlarmList: List<MedicineAlarm>?
    private var onItemClickListener: OnItemClickListener? = null
    fun replaceData(medicineAlarmList: List<MedicineAlarm>?) {
        this.medicineAlarmList = medicineAlarmList
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view: View =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.row_medicine, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicineAlarm: MedicineAlarm = medicineAlarmList!![position] ?: return
        holder.tvMedTime.setText(medicineAlarm.getStringTime())
        holder.tvMedicineName.setText(medicineAlarm.getPillName())
        holder.tvDoseDetails.setText(medicineAlarm.getFormattedDose())
        holder.ivAlarmDelete!!.visibility = View.VISIBLE
        holder.ivAlarmDelete!!.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener!!.onMedicineDeleteClicked(medicineAlarm)
            }
        }
    }

    val itemCount: Int
        get() = if (medicineAlarmList != null && !medicineAlarmList!!.isEmpty()) medicineAlarmList!!.size else 0

    class MedicineViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_med_time)
        var tvMedTime: RobotoBoldTextView? = null

        @BindView(R.id.tv_medicine_name)
        var tvMedicineName: RobotoBoldTextView? = null

        @BindView(R.id.tv_dose_details)
        var tvDoseDetails: RobotoRegularTextView? = null

        @BindView(R.id.iv_medicine_action)
        var ivMedicineAction: ImageView? = null

        @BindView(R.id.iv_alarm_delete)
        var ivAlarmDelete: ImageView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    interface OnItemClickListener {
        fun onMedicineDeleteClicked(medicineAlarm: MedicineAlarm?)
    }

    init {
        this.medicineAlarmList = medicineAlarmList
    }
}