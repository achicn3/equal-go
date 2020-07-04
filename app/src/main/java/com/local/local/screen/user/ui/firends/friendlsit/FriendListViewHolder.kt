package com.local.local.screen.user.ui.firends.friendlsit

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R

class FriendListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivFriendsAvatar = itemView.findViewById<ImageView>(R.id.iv_friendList_avatar)
    val tvFriendsName = itemView.findViewById<TextView>(R.id.tv_friendList_name)
    val tvFriendsDistance = itemView.findViewById<TextView>(R.id.tv_friendList_distance)
}