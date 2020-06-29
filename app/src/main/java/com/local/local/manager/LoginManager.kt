package com.local.local.manager

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.util.FirebaseUtil

class LoginManager private constructor() {
    companion object {
        val instance: LoginManager by lazy { Holder.INSTANCE }
        private val EXPIRE_SP = "userExpireSharePreference.pref"
        private val EXPIRE_TIME = "LoginExpireTime"
    }

    object Holder {
        val INSTANCE = LoginManager()
    }

    var firebaseUser: FirebaseUser? = null
    var userData : UserInfo? = null
    private val listeners = HashSet<LoginListener?>()

    private val firebaseCallback = object : FirebaseCallback(){
        override fun getUserInfoByPhone(userInfo: UserInfo?) {
            super.getUserInfoByPhone(userInfo)
            userData = userInfo
            dispatchUserInfoChanged()
            dispatchLogStateChanged()
        }
    }

    interface LoginListener {
        fun onLogStateChange()
        fun onUserInfoChange()
    }

    private fun getExpireSP(context: Context) =
        context.getSharedPreferences(EXPIRE_SP, Context.MODE_PRIVATE)

    private fun getLoginExpireTime(context: Context): Long {
        return run {
            getExpireSP(context).getLong(EXPIRE_TIME, 0L)
        }
    }

    private fun setLoginExpireTime(context: Context, time: Long?) {
        run {
            getExpireSP(context).edit()
        }.apply {
            time?.let {
                putLong(EXPIRE_TIME, it)
                commit()
            }
        }
    }

    private fun dispatchUserInfoChanged() {
        val iterator: MutableIterator<LoginListener?> = listeners.iterator()
        while (iterator.hasNext()) {
            iterator.next()?.onUserInfoChange()
        }
    }

    private fun dispatchLogStateChanged() {
        val iterator: MutableIterator<LoginListener?> = listeners.iterator()
        while (iterator.hasNext()) {
            iterator.next()?.onLogStateChange()
        }
    }

    fun isLogin(): Boolean = Firebase.auth.currentUser == null

    fun logout() {
        firebaseUser = null
        Firebase.auth.signOut()
        userData = null
        dispatchLogStateChanged()
        dispatchUserInfoChanged()
    }

    fun loadData(context: Context,firebaseUser: FirebaseUser?){
        this.firebaseUser = firebaseUser ?: return
        FirebaseUtil.getUserInfoByPhone(firebaseUser.phoneNumber,firebaseCallback)
        setLoginExpireTime(context, 6 * 60 * 60 + System.currentTimeMillis())
    }

    fun addListener(l : LoginListener){
        listeners.add(l)
    }

    fun removeListener(l: LoginListener){
        listeners.remove(l)
    }



}