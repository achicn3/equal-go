package com.local.local.screen.user.ui.points.detailstatics

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class PointsValueFormatter: ValueFormatter() {
    private val format = DecimalFormat("###,##0")
    override fun getBarLabel(barEntry: BarEntry?): String {
        return format.format(barEntry?.y) + "點"
    }
}