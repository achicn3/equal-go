package com.local.local.screen.admin.userlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.UserInfo
import com.local.local.extensions.Extensions.loadCircleImage

class UserListAdapter(
    private val context: Context,
    private val userList: ArrayList<UserInfo>
) : RecyclerView.Adapter<UserListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        return UserListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_admin_userlist, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        with(userList[position]){
            holder.avatar.loadCircleImage(context,avatarUrl)
            holder.name.text = name
            holder.phone.text = phone
            holder.leftPoints.text = context.getString(R.string.admin_userList_left_points,points)
        }
    }

}