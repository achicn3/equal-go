package com.local.local.screen.admin.store

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.StoreInfo
import com.local.local.body.StoreItems
import com.local.local.callback.FirebaseCallback
import com.local.local.event.EventBroadcaster
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.util.FirebaseUtil

class EditStoreViewModel(private val eventManager: EventManager<Event> = EventManager()) :
    ViewModel(),
    EventBroadcaster<EditStoreViewModel.Event> by eventManager {
    sealed class Event {
        class OnDeleteStart(): Event()
        class OnDeleteFinish() : Event()
        class OnDeleteSuc(): Event()
        class OnDeleteFail() : Event()
    }

    private fun Event.send() {
        eventManager.send(this)
    }

    val storeItems = MutableLiveData(mutableListOf<StoreItems>())
    val storeInfo = MutableLiveData(mutableListOf<StoreInfo>())
    val showingStore = MutableLiveData<StoreInfo>()
    var selectedIndex: Int = 0
    private val firebaseCallback = object : FirebaseCallback() {
        override fun retrieveStoreItems(storeItems: List<StoreItems>) {
            this@EditStoreViewModel.storeItems.value?.clear()
            this@EditStoreViewModel.storeItems.value?.addAll(storeItems)
            this@EditStoreViewModel.storeItems.notifyObserver()
        }

        override fun retrieveStoreInfo(storeNames: List<StoreInfo>) {
            storeInfo.value?.clear()
            storeInfo.value?.addAll(storeNames)
            storeInfo.notifyObserver()
        }

        override fun storeRemoveCouponResponse(response: Boolean) {
            Event.OnDeleteFinish().send()
            when(response){
                true -> Event.OnDeleteSuc().send()
                false -> Event.OnDeleteFail().send()
            }
        }

    }

    fun setIndex(randomIndex: Int) {
        val size = storeInfo.value?.size ?: return
        val storeList = storeInfo.value ?: return
        if (storeList.isNotEmpty())
            when {
                randomIndex >= size -> {
                    showingStore.value = storeList[0]
                    selectedIndex = 0
                    retrieveStoreItems(storeList[0])
                }
                randomIndex < 0 -> {
                    showingStore.value = storeList[storeList.lastIndex]
                    selectedIndex = storeList.lastIndex
                    retrieveStoreItems(storeList[storeList.lastIndex])
                }
                else -> {
                    showingStore.value = storeList[randomIndex]
                    selectedIndex = randomIndex
                    retrieveStoreItems(storeList[randomIndex])
                }
            }
    }


    fun retrieveStoreNames() {
        FirebaseUtil.retrieveStoreInfo(firebaseCallback)
    }

    private fun retrieveStoreItems(storeInfo: StoreInfo) {
        FirebaseUtil.retrieveStoreItems(storeInfo, firebaseCallback)
    }

    fun onClickDelete(storeInfo: StoreInfo, storeItems: StoreItems) {
        Event.OnDeleteStart().send()
        FirebaseUtil.storeDeleteCoupon(storeInfo, storeItems, firebaseCallback)
    }
}