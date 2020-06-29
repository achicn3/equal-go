package com.local.local.screen.fragment.ui.firends.friendlsit

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.UserInfo
import com.local.local.extensions.Extensions.loadCircleImage

class FriendListAdapter(private val context: Context, private val friendList: List<UserInfo>) : RecyclerView.Adapter<FriendListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        return FriendListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_friends,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        with(friendList[position]){
            holder.ivFriendsAvatar.loadCircleImage(context,friendList[position].avatarUrl)
            holder.tvFriendsName.text = name
            holder.tvFriendsDistance.text = context.getString(R.string.distance,2)
        }
    }
}