package com.local.local.screen.register

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.local.local.event.EventBroadcaster
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.manager.LoginManager
import com.local.local.util.FirebaseUtil
import java.util.concurrent.TimeUnit

class RegisterViewModel(
    private val context: Context,
    private val activity: Activity,
    private val eventManager: EventManager<Event> = EventManager()
) :
    ViewModel(), EventBroadcaster<RegisterViewModel.Event> by eventManager {

    sealed class Event {
        class OnSmsSendStart() : Event()
        class OnSmsSendFinish() : Event()
        class OnRegisterStart() : Event()
        class OnVerificationStart() : Event()
        class OnVerificationFinish() : Event()
        class OnVerificationSuc() : Event()
        class OnVerificationFail() : Event()
        class OnLRegisterFinish() : Event()
        class OnRegisterSuc() : Event()
        class OnRegisterFail() : Event()
        class OnPhoneExisted() : Event()
        class OnError() : Event()
    }

    private fun Event.send() {
        eventManager.send(this)
    }

    private var firebaseUser: FirebaseUser? = null
    private var verificationID: String? = null
    private var errorCount: Int = 0
    private var isRegistered: Boolean? = false
    private val firebaseCallback = object : FirebaseCallback() {
        override fun isPhoneExisted(phoneNumber: String?, response: Boolean?) {
            super.isPhoneExisted(null,response)
            isRegistered = response
            if (phoneNumber == null){
                Event.OnPhoneExisted().send()
                return
            }
            when (isRegistered) {
                true -> {
                    Event.OnPhoneExisted().send()
                }
                false -> {
                    Event.OnSmsSendStart().send()
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        activity, // Activity (for callback binding)
                        smsCallback
                    )
                }
                else -> {
                    Event.OnError().send()
                }
            }
        }

        override fun registerResponse(userInfo: UserInfo?, response: Boolean?) {
            super.registerResponse(userInfo,response)
            Event.OnLRegisterFinish().send()
            when(response){
                true -> {
                    run{
                        userProfileChangeRequest {
                            displayName = userInfo?.name
                        }
                    }.let{ profileRequest ->
                        firebaseUser?.updateProfile(profileRequest)
                            ?.addOnCompleteListener{ task ->
                                if(task.isSuccessful){
                                    Event.OnRegisterSuc().send()
                                    LoginManager.instance.loadData(context,firebaseUser)
                                }
                                else{
                                    Event.OnRegisterFail().send()
                                }
                            }
                    }
                }
                else -> {
                    Event.OnRegisterFail().send()
                }
            }
        }

    }

    private val auth = Firebase.auth.apply {
        setLanguageCode("zh-TW")
    }

    fun onClickSend(userInfo: UserInfo){
        Event.OnRegisterStart().send()
        FirebaseUtil.sendUserToServer(userInfo,firebaseCallback)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Event.OnVerificationFinish().send()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    firebaseUser = task.result?.user
                    Event.OnVerificationSuc().send()
                } else {
                    task.exception?.printStackTrace()
                    Event.OnVerificationFail().send()
                }
            }
    }

    private val smsCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //GOOGLE PLAY Service或許會自動攔截，若有攔截到可以直接登入
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        }

        //發送簡訊失敗(可能原因:手機格是錯誤)
        override fun onVerificationFailed(e: FirebaseException) {
            e.printStackTrace()
            Event.OnSmsSendFinish().send()
            errorCount += 1
        }

        //開始發送簡訊
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            Event.OnSmsSendFinish().send()
            verificationID = p0
        }
    }

    fun onClickConfirm(userInputCode: String) {
        Event.OnVerificationStart().send()
        verificationID?.run {
            PhoneAuthProvider.getCredential(this, userInputCode)
        }?.also { credential ->
            signInWithPhoneAuthCredential(credential)
        }
    }


    private fun checkPhoneExisted(phoneNumber: String) {
        FirebaseUtil.isPhoneExisted(phoneNumber, firebaseCallback)
    }

    fun onClickSendSmsMessage(phoneNumber: String) {
        checkPhoneExisted(phoneNumber)
    }

}