package com.local.local.screen.user.ui.profile

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R

class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val defaultIconFromFirebase = itemView.findViewById<ImageView>(R.id.rv_profile_profileIcon)
}