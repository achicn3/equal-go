package com.local.local.screen.store.record

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.StoreTransactionRecordBody
import com.local.local.screen.dialog.MonthYearPickerDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class StoreRecordFragment : Fragment(), MonthYearPickerDialog.DateListener {
    private val viewModel: StoreRecordViewModel by viewModel()

    companion object {
        private const val datePickerTag = "DatePickerTag"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store_record, container, false)
    }

    private fun Int.format(): String = if (this < 10) "0$this" else "$this"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity ?: return super.onViewCreated(view, savedInstanceState)
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.iv_storeRecord_left).setOnClickListener {
            viewModel.decrementMonth()
        }

        view.findViewById<ImageView>(R.id.iv_storeRecord_right).setOnClickListener {
            viewModel.incrementMonth()
        }

        val tvDate = view.findViewById<TextView>(R.id.tv_storeRecord_date).apply {
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    activity.supportFragmentManager.also { fragmentManager ->
                        fragmentManager.findFragmentByTag(datePickerTag) ?: run {
                            MonthYearPickerDialog().apply {
                                setTargetFragment(this@StoreRecordFragment, 0)
                                showNow(fragmentManager, datePickerTag)
                            }
                        }
                    }
                }
            }
        }

        viewModel.calendar.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            val year = it.get(Calendar.YEAR)
            val month = (it.get(Calendar.MONTH) + 1).format()
            viewModel.searchRecord(year.toString(), month)
            tvDate.text = context.getString(R.string.date_format_year_month,year,month)
        })

        val recordList = arrayListOf<StoreTransactionRecordBody>()
        val rvAdapter = StoreRecordAdapter(context, recordList)
        view.findViewById<RecyclerView>(R.id.rv_storeRecord_items).apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.allMonthRecordInfo.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            recordList.clear()
            recordList.addAll(it)
            rvAdapter.notifyDataSetChanged()
        })

    }

    override fun pickListener(year: Int, month: Int) {
        viewModel.setDate(year, month)
    }
}