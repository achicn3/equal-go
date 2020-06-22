package com.local.local.screen.login

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kdanmobile.cloud.event.EventBroadcaster
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.manager.LoginManager
import com.local.local.util.FirebaseUtil
import java.util.concurrent.TimeUnit

class LoginViewModel(
    private val context: Context,
    private val activity: Activity,
    private val eventManager: EventManager<Event> = EventManager()
) :
    ViewModel(), EventBroadcaster<LoginViewModel.Event> by eventManager {
    companion object {
        private val USER_SHARE_PREFERENCE = "UserSharePreference.pref"
        private val USER_PHONE_TAG = "userCellphone"
    }

    private fun getSharePreference() =
        context.getSharedPreferences(USER_SHARE_PREFERENCE, Context.MODE_PRIVATE)

    var phoneNumber: String
        get() {
            return getSharePreference().getString(USER_PHONE_TAG, null) ?: ""
        }
        set(value) {
            getSharePreference().edit().run {
                putString(USER_PHONE_TAG, value)
                commit()
            }
        }

    private val auth = Firebase.auth.apply {
        setLanguageCode("zh-TW")
    }

    private var firebaseUser : FirebaseUser? = null

    sealed class Event {
        class OnSmsSendStart() : Event()
        class OnSmsSendFinish() : Event()
        class OnLoginStart() : Event()
        class OnLoginFinish() : Event()
        class OnLoginSuc() : Event()
        class OnLoginFail() : Event()
        class OnUserNotRegister() : Event()
        class OnError() : Event()
    }

    private fun Event.send() {
        eventManager.send(this)
    }

    private var verificationID: String? = null
    private var userData: UserInfo? = null
    private val smsCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //GOOGLE PLAY Service或許會自動攔截，若有攔截到可以直接登入
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        }

        //發送簡訊失敗(可能原因:手機格是錯誤)
        override fun onVerificationFailed(e: FirebaseException) {
            e.printStackTrace()
            Event.OnError().send()
        }

        //開始發送簡訊
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            Event.OnSmsSendFinish().send()
            verificationID = p0
        }
    }

    private val loginCallback = object : FirebaseCallback() {
        override fun isPhoneExisted(phoneNumber: String?, response: Boolean?) {
            super.isPhoneExisted(phoneNumber, response)
            when (response) {
                true -> {
                    phoneNumber ?: return
                    Event.OnSmsSendStart().send()
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        activity, // Activity (for callback binding)
                        smsCallback
                    )
                }
                false -> {
                    Event.OnUserNotRegister().send()
                }
                null -> {
                    Event.OnError().send()
                }
            }
        }

        override fun getUserInfoByPhone(userInfo: UserInfo?) {
            super.getUserInfoByPhone(userInfo)
            userData = userInfo
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Event.OnLoginFinish().send()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    firebaseUser = task.result?.user
                    FirebaseUtil.getUserInfoByPhone(firebaseUser?.phoneNumber, loginCallback)
                    LoginManager.instance.loadData(context, firebaseUser)
                    Event.OnLoginSuc().send()
                } else {
                    task.exception?.printStackTrace()
                    Event.OnLoginFail().send()
                }
            }
    }

    fun onClickLogin(code: String){
        Event.OnLoginStart().send()
        if (verificationID == null)
            Event.OnLoginFail().send()
        verificationID?.run{
            PhoneAuthProvider.getCredential(this,code)
        }?.let {
            signInWithPhoneAuthCredential(it)
        }
    }

    fun onClickSendSms(phoneNumber: String) {
        FirebaseUtil.isPhoneExisted(phoneNumber, loginCallback)
    }
}