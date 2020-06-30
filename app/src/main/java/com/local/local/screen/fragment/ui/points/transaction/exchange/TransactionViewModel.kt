package com.local.local.screen.fragment.ui.points.transaction.exchange

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.event.EventBroadcaster
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.StoreInfo
import com.local.local.body.StoreItems
import com.local.local.body.TransactionInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.manager.LoginManager
import com.local.local.util.FirebaseUtil
import java.text.SimpleDateFormat
import java.util.*


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

    val storeItems = MutableLiveData(mutableListOf<StoreItems>())
    val storeInfo = MutableLiveData(mutableListOf<StoreInfo>())
    val showingStoreName = MutableLiveData<String>()
    var selectedIndex : Int = 0
    private val firebaseCallback = object : FirebaseCallback() {
        override fun retrieveStoreItems(storeItems: List<StoreItems>) {
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
                selectedIndex = 0
                retrieveStoreItems(value[0])
            }
            randomIndex < 0 -> {
                showingStoreName.value = value[value.lastIndex].storeName
                selectedIndex = value.lastIndex
                retrieveStoreItems(value[value.lastIndex])
            }
            else -> {
                showingStoreName.value = value[randomIndex].storeName
                selectedIndex = randomIndex
                retrieveStoreItems(value[randomIndex])
            }
        }
    }

    fun onClickExchange(position: Int) {
        val exchangeItems = storeItems.value?.get(position) ?: return
        val needPoints = exchangeItems.needPoints
        val size = storeItems.value?.size ?: 0
        if (position >= size) return
        Event.OnUpdateStart().send()
        val leftPoints = LoginManager.instance.userData?.updatePoints(-needPoints) ?: return
        val date = Calendar.getInstance(Locale.TAIWAN).run {
            SimpleDateFormat("YYYY/mm/dd").format(this.time).split("/")
        }

        val transactionInfo = TransactionInfo(exchangeItems.storeName,exchangeItems.storeType,date[0],date[1],date[2],leftPoints)
        FirebaseUtil.addTransactionInfo(transactionInfo)
        FirebaseUtil.updateUserInfo(firebaseCallback)
    }

    fun retrieveStoreNames() {
        FirebaseUtil.retrieveStoreInfo(firebaseCallback)
    }

    private fun retrieveStoreItems(storeInfo: StoreInfo) {
        FirebaseUtil.retrieveStoreItems(storeInfo, firebaseCallback)
    }
}