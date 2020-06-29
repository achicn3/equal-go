package com.local.local.screen.fragment.ui.points.detailstatics

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class DistanceValueFormatter : ValueFormatter() {
    private val format = DecimalFormat("###,##0.0")
    override fun getBarLabel(barEntry: BarEntry?): String {
        return format.format(barEntry?.y?.div(1000)) + " 公里"
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return format.format(value/1000)
    }

}