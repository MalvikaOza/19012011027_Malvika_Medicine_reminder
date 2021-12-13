package com.example.medicinetime.medicine

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.medicinetime.addmedicine.AddMedicineActivity
import java.util.*


class MedicineFragment : Fragment(), MedicineContract.View, OnItemClickListener {
    @BindView(R.id.medicine_list)
    var rvMedList: RecyclerView? = null
    var unbinder: Unbinder? = null

    @BindView(R.id.noMedIcon)
    var noMedIcon: ImageView? = null

    @BindView(R.id.noMedText)
    var noMedText: RobotoLightTextView? = null

    @BindView(R.id.add_med_now)
    var addMedNow: TextView? = null

    @BindView(R.id.no_med_view)
    var noMedView: View? = null

    @BindView(R.id.progressLoader)
    var progressLoader: ProgressBar? = null
    private var presenter: MedicineContract.Presenter? = null
    private var medicineAdapter: MedicineAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        medicineAdapter = MedicineAdapter(ArrayList<E>(0))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_medicine, container, false)
        unbinder = ButterKnife.bind(this, view)
        setAdapter()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fab: FloatingActionButton =
            Objects.requireNonNull(activity).findViewById(R.id.fab_add_task)
        fab.setImageResource(R.drawable.ic_add)
        fab.setOnClickListener(View.OnClickListener { v: View? -> presenter!!.addNewMedicine() })
    }

    private fun setAdapter() {
        rvMedList.setAdapter(medicineAdapter)
        rvMedList.setLayoutManager(LinearLayoutManager(context))
        rvMedList.setHasFixedSize(true)
        medicineAdapter!!.setOnItemClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]
        presenter!!.onStart(day)
    }

    fun setPresenter(presenter: MedicineContract.Presenter?) {
        this.presenter = presenter
    }

    override fun showLoadingIndicator(active: Boolean) {
        if (view == null) {
            return
        }
        progressLoader.setVisibility(if (active) View.VISIBLE else View.GONE)
    }

    override fun showMedicineList(medicineAlarmList: List<MedicineAlarm>?) {
        medicineAdapter!!.replaceData(medicineAlarmList)
        rvMedList.setVisibility(View.VISIBLE)
        noMedView!!.visibility = View.GONE
    }

    override fun showAddMedicine() {
        val intent = Intent(context, AddMedicineActivity::class.java)
        startActivityForResult(intent, AddMedicineActivity.REQUEST_ADD_TASK)
    }

    override fun showMedicineDetails(taskId: Long, medName: String?) {
        val intent = Intent(context, AddMedicineActivity::class.java)
        intent.putExtra(AddMedicineActivity.EXTRA_TASK_ID, taskId)
        intent.putExtra(AddMedicineActivity.EXTRA_TASK_NAME, medName)
        startActivity(intent)
    }

    override fun showLoadingMedicineError() {
        showMessage(getString(R.string.loading_tasks_error))
    }

    override fun showNoMedicine() {
        showNoTasksViews(
            resources.getString(R.string.no_medicine_added)
        )
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_me_message))
    }

    override fun showMedicineDeletedSuccessfully() {
        showMessage(getString(R.string.successfully_deleted_message))
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]
        presenter!!.onStart(day)
    }

    private fun showMessage(message: String) {
        if (view != null) Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    override val isActive: Boolean
        get() = isAdded

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    @OnClick(R.id.add_med_now)
    fun addMedicine() {
        showAddMedicine()
    }

    private fun showNoTasksViews(mainText: String) {
        rvMedList.setVisibility(View.GONE)
        noMedView!!.visibility = View.VISIBLE
        noMedText.setText(mainText)
        noMedIcon!!.setImageDrawable(resources.getDrawable(R.drawable.icon_my_health))
        addMedNow.setVisibility(View.VISIBLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter!!.result(requestCode, resultCode)
    }

    fun onMedicineDeleteClicked(medicineAlarm: MedicineAlarm?) {
        presenter!!.deleteMedicineAlarm(medicineAlarm, activity)
    }

    companion object {
        fun newInstance(): MedicineFragment {
            val args = Bundle()
            val fragment = MedicineFragment()
            fragment.arguments = args
            return fragment
        }
    }
}