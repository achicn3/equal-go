package com.local.local.screen.store.record

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.StoreTransactionRecordBody
import com.local.local.extensions.Extensions.loadCircleImage

class StoreRecordAdapter(
    private val context: Context,
    private val records: ArrayList<StoreTransactionRecordBody>
) :
    RecyclerView.Adapter<StoreRecordViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreRecordViewHolder {
        return StoreRecordViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_store_record_items, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: StoreRecordViewHolder, position: Int) {
        with(records[position]) {
            holder.tvName.text = userName
            holder.tvPhone.text = userPhone
            holder.tvDescription.text = context.getString(R.string.store_transactionRecord_userExchangeItem,itemDescription)
            holder.ivAvatar.loadCircleImage(context, userAvatarUrl)
        }
    }

}