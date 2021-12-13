package com.example.medicinetime.report

import android.R
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.example.medicinetime.Injection

class MonthlyReportActivity : AppCompatActivity() {
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    private var presenter: MonthlyReportPresenter? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_report)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //Create Fragment
        var monthlyReportFragment: MonthlyReportFragment =
            getSupportFragmentManager().findFragmentById(R.id.contentFrame)
        if (monthlyReportFragment == null) {
            monthlyReportFragment = MonthlyReportFragment.newInstance()
            ActivityUtils.addFragmentToActivity(
                getSupportFragmentManager(),
                monthlyReportFragment,
                R.id.contentFrame
            )
        }

        //Create TaskPresenter
        presenter = MonthlyReportPresenter(
            Injection.provideMedicineRepository(this@MonthlyReportActivity),
            monthlyReportFragment
        )

        //Load previous saved Instance
        if (savedInstanceState != null) {
            presenter.setFiltering(savedInstanceState.getSerializable(CURRENT_FILTERING_TYPE))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    protected override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(CURRENT_FILTERING_TYPE, presenter.getFilterType())
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val CURRENT_FILTERING_TYPE = "current_filtering_type"
    }
}