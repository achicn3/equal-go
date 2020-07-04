package com.local.local.screen.user.ui.points.transaction.exchange

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.StoreItems
import com.local.local.extensions.Extensions.loadImage

class TransactionAdapter(private val context: Context,
                         private val transItems: List<StoreItems>) :
        RecyclerView.Adapter<TransactionItemViewHolder>() {
    interface ClickListener {
        fun onClickConfirm(position: Int)
    }

    var clickListener: ClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionItemViewHolder {
        return TransactionItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.view_transaction_item,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        return transItems.size
    }

    override fun onBindViewHolder(holder: TransactionItemViewHolder, position: Int) {
        with(transItems[position]) {
            holder.ivItems.loadImage(context, imgUrl)
            holder.tvDescription.text = description
            holder.tvNeedPoints.text = context.getString(R.string.needPoints,needPoints)
            holder.btnConfirm.setOnClickListener {
                clickListener?.onClickConfirm(position)
            }
        }
    }
}