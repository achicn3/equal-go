package com.local.local.screen.admin.verification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.body.StoreLoginRegisterBody
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.util.FirebaseUtil

class VerificationViewModel : ViewModel() {
    val verificationStores = MutableLiveData(arrayListOf<StoreLoginRegisterBody>())
    init {
        val firebaseCallback = object : FirebaseCallback(){
            override fun adminRetrieveVerificationStore(storeList: ArrayList<StoreLoginRegisterBody>) {
                verificationStores.value?.clear()
                verificationStores.value?.addAll(storeList)
                verificationStores.notifyObserver()
            }
        }
        retrieveVerificationStore(firebaseCallback)
    }
    private fun retrieveVerificationStore(firebaseCallback: FirebaseCallback){
        FirebaseUtil.adminRetrieveVerificationStore(firebaseCallback)
    }

}