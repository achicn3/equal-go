package com.local.local.screen.store.register

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.StoreInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.event.EventBroadcaster
import com.local.local.util.FirebaseUtil

class StoreRegisterViewModel(
    private val eventManager: EventManager<Event> = EventManager()
) : ViewModel(), EventBroadcaster<StoreRegisterViewModel.Event> by eventManager {
    sealed class Event {
        class OnRegisterStart : Event()
        class OnRegisterFinish : Event()
        class OnWaitingConfirm : Event()
        class OnRegisterInfoSendSuc : Event()
        class OnRegisterInfoSendFail : Event()
    }

    private fun Event.send() {
        eventManager.send(this)
    }


    private var email: String = ""
    private var pwd: String = ""
    private var storeInfo: StoreInfo? = null

    private val firebaseCallback = object : FirebaseCallback() {
        override fun storeSendRegisterInfoResponse(response: Boolean) {
            super.storeSendRegisterInfoResponse(response)
            Event.OnRegisterFinish().send()
            when (response) {
                true -> {
                    Event.OnRegisterInfoSendSuc().send()
                }
                false -> {
                    Event.OnRegisterInfoSendFail().send()
                }
            }
        }

        override fun storeCheckWaitingConfirm(response: Boolean) {
            val storeInfo = storeInfo ?: return
            Log.d("status","the waiting response is $response")
            if (!response)
                FirebaseUtil.storeSendRegisterInfoToAdmin(email, pwd, storeInfo, this)
            else {
                Event.OnRegisterFinish().send()
                Event.OnWaitingConfirm().send()
            }
        }
    }

    fun onClickConfirm(storeName: String, email: String, pwd: String, type: String) {
        Event.OnRegisterStart().send()
        val key = FirebaseUtil.getKey() ?: return
        val storeInfo = StoreInfo(storeName, type, key)
        this.storeInfo = storeInfo
        this.email = email
        this.pwd = pwd
        FirebaseUtil.storeCheckIfWaitingConfirm(email, firebaseCallback)
    }
}