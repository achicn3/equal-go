package com.local.local.screen.fragment.ui.home.detailstatics

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
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.local.local.R
import com.local.local.body.RecordInfo
import com.local.local.screen.fragment.dialog.MonthYearPickerDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

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
        val month = get(Calendar.MONTH)+1
        val monthStr = if(month<=9) "0$month" else "$month"
        return getString(R.string.date_format_year_month,year,monthStr)
    }

    private fun generateBarData(list: List<RecordInfo?>) : BarData {
        val entries = arrayListOf<BarEntry>()
        list.forEach { info ->
            val days = info?.days ?: return@forEach
            entries.add(BarEntry(days.toFloat(),info.distance))
        }
        val dataSet = BarDataSet(entries,"日期").apply {
            colors = ColorTemplate.VORDIPLOM_COLORS.toList()
            highLightAlpha = 255
        }
        return BarData(dataSet).apply {
            barWidth = 0.9f
        }
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
        val barChart = view.findViewById<BarChart>(R.id.view_detailStatics_barChart).apply {
            renderer = CustomBarChartRender(this,animator,viewPortHandler).apply {
                setRadius(20)
            }
        }
        barChart.xAxis.apply {
            granularity = 1f
        }

        val barItems = arrayListOf<BarEntry>()
        val pieItems = arrayListOf<PieEntry>()

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
            val year = it.get(Calendar.YEAR)
            val month = it.get(Calendar.MONTH) + 1
            viewModel.searchRecord(year, month)
        })

        viewModel.allMonthRecordInfo.observe(viewLifecycleOwner, Observer { list ->
            list ?: return@Observer
            Log.d("status","allmonthrecord info $list")
            barItems.clear()
            pieItems.clear()
            val barData = generateBarData(list)
            barChart.data = barData
            barChart.data.notifyDataChanged()
            barChart.notifyDataSetChanged()
            barChart.invalidate()
            barChart.animateXY(2000,2000)
        })

    }

    override fun pickListener(year: Int, month: Int) {
        viewModel.setDate(year, month)
    }
}