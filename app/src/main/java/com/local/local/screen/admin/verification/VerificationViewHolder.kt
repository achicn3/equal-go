package com.local.local.screen.admin.verification

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R

class VerificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivType = itemView.findViewById<ImageView>(R.id.iv_verification_type)
    val tvStoreName = itemView.findViewById<TextView>(R.id.tv_verification_storeName)
    val tvStoreEmail = itemView.findViewById<TextView>(R.id.tv_verification_email)
    val btnConfirm = itemView.findViewById<Button>(R.id.btn_verifiaction_confirm)
}