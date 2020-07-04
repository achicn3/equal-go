package com.local.local.screen.user.ui.points.transaction.record

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.TransactionInfo

class TransactionRecordAdapter(private val context: Context, private val records: List<TransactionInfo>) : RecyclerView.Adapter<TransactionRecordViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionRecordViewHolder {
        return TransactionRecordViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.view_user_transaction_record,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: TransactionRecordViewHolder, position: Int) {
        with(records[position]) {
            when (storeType) {
                "食" -> {
                    holder.ivType.setImageResource(R.drawable.ic_transaction_type_food)
                }
                "育" -> {
                    holder.ivType.setImageResource(R.drawable.ic_transaction_type_edu)
                }
                "樂" -> {
                    holder.ivType.setImageResource(R.drawable.ic_transaction_type_fun)
                }
            }
            holder.tvDates.text = context.getString(R.string.transaction_record_date,year,month,day)
            holder.tvDescription.text = context.getString(R.string.transaction_record_description,productDescription)
            holder.tvLeftPoints.text = context.getString(R.string.transaction_record_left_points,leftPoints)
            holder.tvStoreName.text = context.getString(R.string.transaction_record_storeName,storeName)
        }
    }

}