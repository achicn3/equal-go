package com.local.local.screen.fragment.ui.profile

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import kotlinx.android.synthetic.main.fragment_profileinfo.view.*

class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val defaultIconFromFirebase = itemView.findViewById<ImageView>(R.id.rv_profile_profileIcon)
}