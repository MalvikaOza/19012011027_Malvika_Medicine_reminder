package com.example.medicinetime.addmedicine

import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.example.medicinetime.Injection

class AddMedicineActivity : AppCompatActivity() {
    private var mAddMedicinePresenter: com.example.medicinetime.addmedicine.AddMedicinePresenter? =
        null
    private var mActionBar: ActionBar? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)

        //Setup toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        mActionBar = getSupportActionBar()
        mActionBar!!.setDisplayHomeAsUpEnabled(true)
        mActionBar!!.setDisplayShowHomeEnabled(true)
        var addMedicineFragment: com.example.medicinetime.addmedicine.AddMedicineFragment =
            getSupportFragmentManager().findFragmentById(R.id.contentFrame)
        val medId: Int = getIntent().getIntExtra(
            com.example.medicinetime.addmedicine.AddMedicineFragment.Companion.ARGUMENT_EDIT_MEDICINE_ID,
            0
        )
        val medName: String =
            getIntent().getStringExtra(com.example.medicinetime.addmedicine.AddMedicineFragment.Companion.ARGUMENT_EDIT_MEDICINE_NAME)
        setToolbarTitle(medName)
        if (addMedicineFragment == null) {
            addMedicineFragment =
                com.example.medicinetime.addmedicine.AddMedicineFragment.Companion.newInstance()
            if (getIntent().hasExtra(com.example.medicinetime.addmedicine.AddMedicineFragment.Companion.ARGUMENT_EDIT_MEDICINE_ID)) {
                val bundle = Bundle()
                bundle.putInt(
                    com.example.medicinetime.addmedicine.AddMedicineFragment.Companion.ARGUMENT_EDIT_MEDICINE_ID,
                    medId
                )
                addMedicineFragment.setArguments(bundle)
            }
            ActivityUtils.addFragmentToActivity(
                getSupportFragmentManager(),
                addMedicineFragment, R.id.contentFrame
            )
        }
        var shouldLoadDataFromRepo = true
        // Prevent the presenter from loading data from the repository if this is a config change.
        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY)
        }

//        // Create the presenter
        mAddMedicinePresenter = com.example.medicinetime.addmedicine.AddMedicinePresenter(
            medId,
            Injection.provideMedicineRepository(getApplicationContext()),
            addMedicineFragment,
            shouldLoadDataFromRepo
        )
    }

    fun setToolbarTitle(medicineName: String?) {
        if (medicineName == null) {
            mActionBar.setTitle(getString(R.string.new_medicine))
        } else {
            mActionBar!!.title = medicineName
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, mAddMedicinePresenter!!.isDataMissing())
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val REQUEST_ADD_TASK = 1
        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"
        const val EXTRA_TASK_ID = "task_extra_id"
        const val EXTRA_TASK_NAME = "task_extra_name"
    }
}