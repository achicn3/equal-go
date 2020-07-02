package com.local.local.screen.store.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kdanmobile.cloud.event.EventManager
import com.local.local.callback.FirebaseCallback
import com.local.local.event.EventBroadcaster
import com.local.local.util.FirebaseUtil

class StoreLoginViewModel(private val activity: Activity, private val eventManager: EventManager<Event> = EventManager()) :
    ViewModel(), EventBroadcaster<StoreLoginViewModel.Event> by eventManager {

    sealed class Event {
        class OnLoginStart() : Event()
        class OnLoginFinish() : Event()
        class OnLoginSuc() : Event()
        class OnLoginFail() : Event()
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
                    auth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(activity){ task ->
                        if(task.isSuccessful){
                            FirebaseUtil.storeLogin(email,pwd,this)
                        }else{
                            Event.OnLoginFail()
                                .send()
                        }
                    }
                }
                false ->{
                    Event.OnLoginFail()
                        .send()
                }
            }
        }

        override fun storeLoginResponse(response: Boolean) {
            when(response){
                true -> Event.OnLoginSuc().send()
                false -> Event.OnLoginFail().send()
            }
        }
    }

    fun onClickLogin(account: String, pwd: String) {
        Event.OnLoginStart()
            .send()
        email = account
        this.pwd = pwd
        FirebaseUtil.storeCheckIfExisted(account,firebaseCallback)
    }
}