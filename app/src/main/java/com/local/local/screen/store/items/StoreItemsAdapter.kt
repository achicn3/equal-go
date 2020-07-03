package com.local.local.screen.store.items

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.StoreItems
import com.local.local.extensions.Extensions.loadImage

class StoreItemsAdapter(
    private val context: Context,
    private val storeItems: ArrayList<StoreItems>
) : RecyclerView.Adapter<StoreItemsViewHolder>() {
    interface ClickListener{
        fun onClickEdit(storeItems: StoreItems)
    }
    lateinit var listener : ClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemsViewHolder {
        return StoreItemsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_transaction_item,parent,false))
    }

    override fun getItemCount(): Int {
        return storeItems.size
    }

    override fun onBindViewHolder(holder: StoreItemsViewHolder, position: Int) {
        with(storeItems[position]){
            holder.tvProductDescription.text = description
            holder.ivProductImg.loadImage(context,imgUrl)
            holder.btnEdit.setOnClickListener {
                listener.onClickEdit(this)
            }
            holder.tvNeedPoints.text = context.getString(R.string.needPoints,needPoints)
        }
    }
}