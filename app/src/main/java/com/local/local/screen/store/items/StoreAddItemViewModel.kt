package com.local.local.screen.store.items

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.body.StoreItems
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.manager.StoreLoginManager
import com.local.local.util.FirebaseUtil

class StoreAddItemViewModel : ViewModel() {
    val storeItems = MutableLiveData(arrayListOf<StoreItems>())
    private val firebaseCallback = object : FirebaseCallback() {
        override fun retrieveStoreItems(storeItems: List<StoreItems>) {
            this@StoreAddItemViewModel.storeItems.value?.clear()
            this@StoreAddItemViewModel.storeItems.value?.addAll(storeItems)
            this@StoreAddItemViewModel.storeItems.notifyObserver()
        }
    }

    fun retrieveStoreItems(){
        val storeInfo = StoreLoginManager.instance.storeInfo ?: return
        FirebaseUtil.retrieveStoreItems(storeInfo, firebaseCallback)
    }

}