package com.local.local.screen.fragment.ui.points.transaction.exchange

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import kotlinx.android.synthetic.main.view_transaction_item.view.*

class TransactionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
    val ivItems : ImageView = itemView.findViewById(R.id.iv_transaction_item)
    val tvDescription: TextView = itemView.findViewById(R.id.tv_transaction_description)
    val btnConfirm : Button = itemView.findViewById(R.id.btn_transaction_confirm)
    val tvNeedPoints : TextView = itemView.findViewById(R.id.tv_transaction_needPoints)
}