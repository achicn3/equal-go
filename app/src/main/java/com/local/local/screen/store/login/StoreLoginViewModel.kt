package com.local.local.screen.store.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.LoginRegisterBody
import com.local.local.body.StoreInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.event.EventBroadcaster
import com.local.local.manager.StoreLoginManager
import com.local.local.util.FirebaseUtil

class StoreLoginViewModel(private val activity: Activity, private val eventManager: EventManager<Event> = EventManager()) :
    ViewModel(), EventBroadcaster<StoreLoginViewModel.Event> by eventManager {

    sealed class Event {
        class OnLoginStart : Event()
        class OnLoginFinish : Event()
        class OnLoginSuc : Event()
        class OnLoginFail : Event()
        class OnAccountNotExist: Event()
        class OnAdminLoginSuc : Event()
        class OnAdminLoginFail : Event()
    }

    private val auth = Firebase.auth.apply {
        setLanguageCode("zh-TW")
    }
    private var email :String = ""
    private var pwd : String =""
    private fun Event.send() {
        eventManager.send(this)
    }

    private val firebaseCallback = object : FirebaseCallback(){
        override fun storeCheckRegistered(registered: Boolean) {
            super.storeCheckRegistered(registered)
            Event.OnLoginFinish()
                .send()
            when(registered){
                true -> {
                    FirebaseUtil.storeLogin(email,pwd,this)
                }
                false ->{
                    Event.OnAccountNotExist()
                        .send()
                }
            }
        }

        override fun storeLoginResponse(response: Boolean, body: LoginRegisterBody?) {
            when(response){
                true -> {
                    StoreLoginManager.instance.storeInfo = body?.storeInfo
                    Log.d("status","storeLogin succ and the info is ${body?.storeInfo} ")
                    Event.OnLoginSuc().send()

                }
                false -> Event.OnLoginFail().send()
            }
        }

        override fun adminLoginResponse(response: Boolean) {
            Event.OnLoginFinish().send()
            when(response){
                true -> Event.OnAdminLoginSuc().send()
                false -> Event.OnAdminLoginFail().send()
            }
        }

    }

    fun onClickLogin(account: String, pwd: String) {
        Event.OnLoginStart()
            .send()
        email = account
        this.pwd = pwd
        if(email != "boliagogo@gmail.com")
            FirebaseUtil.storeCheckIfExisted(account,firebaseCallback)
        else
            FirebaseUtil.adminLogin(account,pwd, firebaseCallback)
    }
}