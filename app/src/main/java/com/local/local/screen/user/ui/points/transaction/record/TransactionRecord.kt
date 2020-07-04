package com.local.local.screen.user.ui.points.transaction.record

import android.os.Bundle
import android.util.Log
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
import com.local.local.body.TransactionInfo
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class TransactionRecord : Fragment(){

    private fun Int.format(): String = if (this < 10) "0$this" else "$this"

    private fun Calendar.getFormatDate(): String {
        val year = get(Calendar.YEAR)
        val month = (get(Calendar.MONTH) + 1).format()
        val day = get(Calendar.DAY_OF_MONTH).format()
        return getString(R.string.date_format, year, month, day)
    }

    private val viewModel: TransactionRecordViewModel by viewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val records = mutableListOf<TransactionInfo>()
        val rvAdapter =
            TransactionRecordAdapter(
                context,
                records
            )
        val tvDate = view.findViewById<TextView>(R.id.tv_record_date)
        val rvRecords = view.findViewById<RecyclerView>(R.id.rv_record_items).apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)
        }
        view.findViewById<ImageView>(R.id.iv_record_left).setOnClickListener {
            viewModel.decrementDay()
        }
        view.findViewById<ImageView>(R.id.iv_record_right).setOnClickListener {
            viewModel.incrementDay()
        }

        viewModel.calendar.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            tvDate.text = it.getFormatDate()
            val year = it.get(Calendar.YEAR).toString()
            val month = (it.get(Calendar.MONTH)+1).format()
            val day = it.get(Calendar.DAY_OF_MONTH).format()
            Log.d("status","in record : year $year month:$month day: $day")
            viewModel.retrieveRecord(year, month, day)
        })

        viewModel.record.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            records.clear()
            records.addAll(it)
            rvAdapter.notifyDataSetChanged()
            rvRecords.scheduleLayoutAnimation()
        })

    }
}