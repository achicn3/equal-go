package com.local.local.screen.fragment.ui.points.transaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.event.EventBroadcaster
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.StoreInfo
import com.local.local.body.TransactionItems
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.manager.LoginManager
import com.local.local.util.FirebaseUtil


@SuppressWarnings("all")
class TransactionViewModel(private val eventManager: EventManager<Event> = EventManager())
    : ViewModel(), EventBroadcaster<TransactionViewModel.Event> by eventManager {
    sealed class Event {
        class OnUpdateStart() : Event()
        class OnUpdateFinish() : Event()
        class OnUpdateSuc() : Event()
        class OnUpdateFail() : Event()
    }

    private fun Event.send() {
        eventManager.send(this)
    }

    val storeItems = MutableLiveData(mutableListOf<TransactionItems>())
    val storeInfo = MutableLiveData(mutableListOf<StoreInfo>())
    val showingStoreName = MutableLiveData<String>()
    private val firebaseCallback = object : FirebaseCallback() {
        override fun retrieveStoreItems(storeItems: List<TransactionItems>) {
            this@TransactionViewModel.storeItems.value?.clear()
            this@TransactionViewModel.storeItems.value?.addAll(storeItems)
            this@TransactionViewModel.storeItems.notifyObserver()
        }

        override fun retrieveStoreInfo(storeNames: List<StoreInfo>) {
            storeInfo.value?.clear()
            storeInfo.value?.addAll(storeNames)
            storeInfo.notifyObserver()
        }


        override fun updateUserInfoResponse(isSuccess: Boolean) {
            Event.OnUpdateFinish().send()
            when (isSuccess) {
                true -> {
                    Event.OnUpdateSuc().send()
                }
                else -> {
                    Event.OnUpdateFail().send()
                }
            }
        }

    }

    fun setIndex(randomIndex: Int) {
        val size = storeInfo.value?.size ?: return
        val value = storeInfo.value ?: return
        when {
            randomIndex >= size -> {
                showingStoreName.value = value[0].storeName
                retrieveStoreItems(value[0])
            }
            randomIndex < 0 -> {
                showingStoreName.value = value[value.lastIndex].storeName
                retrieveStoreItems(value[value.lastIndex])
            }
            else -> {
                showingStoreName.value = value[randomIndex].storeName
                retrieveStoreItems(value[randomIndex])
            }
        }
    }

    fun onClickExchange(position: Int) {
        val needPoints = storeItems.value?.get(position)?.needPoints ?: return
        val size = storeItems.value?.size ?: 0
        if (position >= size) return
        Event.OnUpdateStart().send()
        LoginManager.instance.userData?.updatePoints(-needPoints)
        FirebaseUtil.updateUserInfo(firebaseCallback)
    }

    fun retrieveStoreNames() {
        FirebaseUtil.retrieveStoreInfo(firebaseCallback)
    }

    private fun retrieveStoreItems(storeInfo: StoreInfo) {
        FirebaseUtil.retrieveStoreItems(storeInfo.key, firebaseCallback)
    }
}