package com.local.local.screen.admin.userlist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R

class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val avatar = itemView.findViewById<ImageView>(R.id.iv_admin_userList_avatar)
    val name = itemView.findViewById<TextView>(R.id.tv_admin_userList_name)
    val phone = itemView.findViewById<TextView>(R.id.tv_admin_userList_phone)
    val leftPoints = itemView.findViewById<TextView>(R.id.tv_admin_userList_leftPoints)
}