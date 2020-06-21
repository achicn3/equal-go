package com.local.local.manager

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginManager private constructor() {
    companion object {
        val instance: LoginManager by lazy { Holder.INSTANCE }
        private val EXPIRE_SP = "userExpireSharePreference.pref"
        private val EXPIRE_TIME = "LoginExpireTime"
    }

    object Holder {
        val INSTANCE = LoginManager()
    }

    var user: FirebaseUser? = null
    private val listeners = HashSet<LoginListener?>()

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

    fun loadData(context: Context, firebaseUser: FirebaseUser?) {
        user = firebaseUser ?: return
        setLoginExpireTime(context, 6 * 60 * 60 + System.currentTimeMillis())
    }

    private fun logout(context: Context) {
        user = null
        dispatchLogStateChanged()
        dispatchUserInfoChanged()
    }

    fun loadData(firebaseUser: FirebaseUser?){
        user = firebaseUser
        dispatchUserInfoChanged()
        dispatchLogStateChanged()
    }

    /*fun logout(context: Context, callback: AccountCallback) {

    }*/


}