package com.local.local.screen.admin.verification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.StoreLoginRegisterBody
import com.local.local.util.FirebaseUtil

class VerificationAdapter(
    private val storeList: ArrayList<StoreLoginRegisterBody>
) : RecyclerView.Adapter<VerificationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerificationViewHolder {
        return VerificationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_verifiaction_stores, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    override fun onBindViewHolder(holder: VerificationViewHolder, position: Int) {
        with(storeList[position]){
            when(storeInfo?.storeType){
                "食" -> holder.ivType.setBackgroundResource(R.drawable.ic_transaction_type_food)
                "育" -> holder.ivType.setBackgroundResource(R.drawable.ic_transaction_type_edu)
                "樂" -> holder.ivType.setBackgroundResource(R.drawable.ic_transaction_type_fun)
            }
            holder.tvStoreEmail.text = accountID
            holder.tvStoreName.text = storeInfo?.storeName
            holder.btnConfirm.setOnClickListener {
                FirebaseUtil.adminConfirmStoreInfo(this)
            }
        }
    }
}