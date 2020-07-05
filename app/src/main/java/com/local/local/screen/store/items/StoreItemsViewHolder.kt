package com.local.local.screen.store.items

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R

class StoreItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val ivProductImg = itemView.findViewById<ImageView>(R.id.iv_transaction_item)
    val tvProductDescription = itemView.findViewById<TextView>(R.id.tv_transaction_description)
    private val btnExchange = itemView.findViewById<Button>(R.id.btn_transaction_confirm).apply {
        visibility = View.GONE
    }
    val btnEdit = itemView.findViewById<Button>(R.id.btn_transaction_edit).apply {
        visibility = View.VISIBLE
    }
    val btnDelete = itemView.findViewById<Button>(R.id.btn_transaction_delete).apply {
        visibility = View.VISIBLE
    }
    val tvNeedPoints = itemView.findViewById<TextView>(R.id.tv_transaction_needPoints)
}