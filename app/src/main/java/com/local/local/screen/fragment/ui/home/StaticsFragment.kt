package com.local.local.screen.fragment.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.local.local.R
import com.local.local.screen.fragment.dialog.MonthYearPickerDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import androidx.lifecycle.Observer

class StaticsFragment : Fragment(),MonthYearPickerDialog.DateListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail_statics,container,false)
    }
    private val viewModel : StaticsViewModel by viewModel()
    companion object{
        private const val DATE_PICKER_DIALOG_TAG = "DatePickerDialogTAG"
    }

    private fun Calendar.getFormatDate() : String{
        val year = get(Calendar.YEAR)
        val month = get(Calendar.MONTH)
        val monthStr = if(month<=9) "0$month" else "$month"
        return getString(R.string.date_format_year_month,year,monthStr)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return super.onViewCreated(view, savedInstanceState)
        val tvDate = view.findViewById<TextView>(R.id.tv_detailStatics_date)

        view.findViewById<ImageView>(R.id.iv_detailStatics_left).setOnClickListener {
            viewModel.decrementMonth()
        }

        view.findViewById<ImageView>(R.id.iv_detailStatics_right).setOnClickListener {
            viewModel.incrementMonth()
        }

        view.findViewById<LinearLayout>(R.id.viewGroup_detailStatics_date).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                activity.supportFragmentManager.also { fragmentManager ->
                    fragmentManager.findFragmentByTag(DATE_PICKER_DIALOG_TAG) ?: run {
                        MonthYearPickerDialog().apply {
                            setTargetFragment(this@StaticsFragment,0)
                            showNow(fragmentManager, DATE_PICKER_DIALOG_TAG)
                        }
                    }
                }
            }
        }

        viewModel.calendar.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            tvDate.text = it.getFormatDate()
        })

    }



    override fun pickListener(year: Int, month: Int) {
        viewModel.searchRecord(year, month)
    }
}