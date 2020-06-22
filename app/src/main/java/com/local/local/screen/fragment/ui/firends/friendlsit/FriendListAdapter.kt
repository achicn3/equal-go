package com.local.local.screen.fragment.ui.firends.friendlsit

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.local.local.R
import com.local.local.body.UserInfo

class FriendListAdapter(private val context: Context, private val friendList: List<UserInfo>) : RecyclerView.Adapter<FriendListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        return FriendListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_friends,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        with(friendList[position]){
            val cp = CircularProgressDrawable(context)
            cp.strokeWidth = 5f
            cp.centerRadius = 30f
            cp.setColorSchemeColors(R.color.colorGreen)
            cp.start()
            Glide
                .with(context)
                .load(avatarUrl)
                .apply(RequestOptions().circleCrop())
                .placeholder(cp)
                .into(holder.ivFriendsAvatar)

            holder.tvFriendsName.text = name
            holder.tvFriendsDistance.text = context.getString(R.string.distance,2)

        }
    }
}