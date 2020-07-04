package com.local.local.screen.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.local.local.R
import java.lang.Exception
import java.util.*


class MonthYearPickerDialog : DialogFragment() {

    interface DateListener{
        fun pickListener(year: Int, month: Int)
    }

    private var listener: DateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            listener = targetFragment as DateListener
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity ?: return super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(activity)
        // Get the layout inflater
        val inflater: LayoutInflater = activity.layoutInflater
        val cal: Calendar = Calendar.getInstance()
        val dialog = LayoutInflater.from(context).inflate(R.layout.fragment_datepicker, null)
        val monthPicker = dialog.findViewById(R.id.picker_month) as NumberPicker
        val yearPicker = dialog.findViewById(R.id.picker_year) as NumberPicker
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal.get(Calendar.MONTH)
        val year: Int = cal.get(Calendar.YEAR)
        yearPicker.minValue = MIN_YEAR
        yearPicker.maxValue = MAX_YEAR
        yearPicker.value = year
        builder.setView(dialog) // Add action buttons
                .setPositiveButton(R.string.confirm) { _, _ ->
                    listener?.pickListener(yearPicker.value,monthPicker.value)
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    this@MonthYearPickerDialog.dialog?.cancel()
                }
        return builder.create()
    }

    companion object {
        private const val MIN_YEAR = 2020
        private const val MAX_YEAR = 2099
    }
}