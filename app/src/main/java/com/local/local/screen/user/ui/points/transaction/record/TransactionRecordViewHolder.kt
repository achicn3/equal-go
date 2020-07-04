package com.local.local.screen.user.ui.points.transaction.record

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R

class TransactionRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvDescription = itemView.findViewById<TextView>(R.id.tv_transactionRecord_description)
    val tvStoreName = itemView.findViewById<TextView>(R.id.tv_transactionRecord_storeName)
    val ivType = itemView.findViewById<ImageView>(R.id.iv_transactionRecord_type)
    val tvLeftPoints = itemView.findViewById<TextView>(R.id.tv_transactionRecord_leftPoints)
    val tvDates = itemView.findViewById<TextView>(R.id.tv_transactionRecord_date)
}