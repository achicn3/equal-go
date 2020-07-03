package com.local.local.screen.store.record

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R

class StoreRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val ivAvatar = itemView.findViewById<ImageView>(R.id.iv_storeRecord_userAvatar)
    val tvName = itemView.findViewById<TextView>(R.id.tv_storeRecord_userName)
    val tvPhone = itemView.findViewById<TextView>(R.id.tv_storeRecord_userPhone)
}