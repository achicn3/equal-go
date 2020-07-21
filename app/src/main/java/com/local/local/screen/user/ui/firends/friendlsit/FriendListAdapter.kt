package com.local.local.screen.user.ui.firends.friendlsit

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.UserInfo
import com.local.local.extensions.Extensions.loadCircleImage
import com.local.local.manager.UserLoginManager

class FriendListAdapter(private val context: Context, private val friendList: List<UserInfo?>) : RecyclerView.Adapter<FriendListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        return FriendListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_friends, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        with(friendList[position]){
            val userInfo = this ?: return@with
            holder.ivFriendsAvatar.loadCircleImage(context,userInfo.avatarUrl)
            holder.tvFriendsName.text = userInfo.name

            val floatArray = FloatArray(1)
            val myLat = UserLoginManager.instance.userData?.latitude ?: 0.0
            val myLong = UserLoginManager.instance.userData?.longitude ?: 0.0
            val friendLat = latitude ?: 0.0
            val friendLong = longitude ?: 0.0
            Location.distanceBetween(myLat,myLong,friendLat,friendLong,floatArray)
            holder.tvFriendsDistance.text = context.getString(R.string.distance,floatArray[0]/1000)
            holder.tvFriendPoints.text = context.getString(R.string.points,points)
        }
    }
}