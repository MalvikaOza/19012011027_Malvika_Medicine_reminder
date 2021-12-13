package com.example.medicinetime.alarm

import android.R
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.example.medicinetime.Injection

class ReminderActivity : AppCompatActivity() {
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    var mReminderPresenter: ReminderPresenter? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_actvity)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        val intent: Intent = getIntent()
        if (!intent.hasExtra(ReminderFragment.EXTRA_ID)) {
            finish()
            return
        }
        val id: Long = intent.getLongExtra(ReminderFragment.EXTRA_ID, 0)
        var reminderFragment: ReminderFragment =
            getSupportFragmentManager().findFragmentById(R.id.contentFrame)
        if (reminderFragment == null) {
            reminderFragment = ReminderFragment.newInstance(id)
            ActivityUtils.addFragmentToActivity(
                getSupportFragmentManager(),
                reminderFragment,
                R.id.contentFrame
            )
        }

        //Create MedicinePresenter
        mReminderPresenter = ReminderPresenter(
            Injection.provideMedicineRepository(this@ReminderActivity),
            reminderFragment
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            if (mReminderPresenter != null) {
                mReminderPresenter.finishActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (mReminderPresenter != null) {
            mReminderPresenter.finishActivity()
        }
    }
}