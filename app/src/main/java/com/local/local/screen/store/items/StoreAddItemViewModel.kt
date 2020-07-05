package com.local.local.screen.store.items

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.StoreItems
import com.local.local.callback.FirebaseCallback
import com.local.local.event.EventBroadcaster
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.manager.StoreLoginManager
import com.local.local.util.FirebaseUtil

class StoreAddItemViewModel(private val eventManager: EventManager<Event> = EventManager()) :
    ViewModel(), EventBroadcaster<StoreAddItemViewModel.Event> by eventManager {
    sealed class Event {
        class OnDeleteStart() : Event()
        class OnDeleteFinish() : Event()
        class OnDeleteSuc() : Event()
        class OnDeleteFail() : Event()
    }
    val storeItems = MutableLiveData(arrayListOf<StoreItems>())
    private val firebaseCallback = object : FirebaseCallback() {
        override fun retrieveStoreItems(storeItems: List<StoreItems>) {
            this@StoreAddItemViewModel.storeItems.value?.clear()
            this@StoreAddItemViewModel.storeItems.value?.addAll(storeItems)
            this@StoreAddItemViewModel.storeItems.notifyObserver()
        }

        override fun storeRemoveCouponResponse(response: Boolean) {
            Event.OnDeleteFinish().send()
            when(response){
                true -> Event.OnDeleteSuc().send()
                false -> Event.OnDeleteFail().send()
            }
        }
    }

    private fun Event.send(){
        eventManager.send(this)
    }

    fun retrieveStoreItems(){
        val storeInfo = StoreLoginManager.instance.storeInfo ?: return
        FirebaseUtil.retrieveStoreItems(storeInfo, firebaseCallback)
    }

    fun onClickDeleteItems(storeItems: StoreItems) {
        val storeInfo = StoreLoginManager.instance.storeInfo ?: return
        Event.OnDeleteStart().send()
        FirebaseUtil.storeDeleteCoupon(storeInfo, storeItems, firebaseCallback)
    }

}