package com.local.local.screen.user.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.extensions.Extensions.loadCircleImage

class ProfileInfoAdapter(private val context: Context,private val defaultAvatarList: List<String?>) : RecyclerView.Adapter<ProfileViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder
        = ProfileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_profileinfo_avatar,parent,false))

    override fun getItemCount(): Int {
        return  defaultAvatarList.size
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        defaultAvatarList[position]?.run {
            holder.defaultIconFromFirebase.loadCircleImage(context,this)
        }
    }
}