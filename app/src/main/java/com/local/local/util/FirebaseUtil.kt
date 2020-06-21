package com.local.local.util

import android.util.Log
import com.google.firebase.database.*
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUtil {
    companion object{
        private val db = FirebaseDatabase.getInstance().reference

        fun sendUserToServer(userInfo: UserInfo,callback: FirebaseCallback){
            db.child("user").push().setValue(userInfo
            ) { p0, p1 ->
                if(p0 != null){
                    p0.toException().printStackTrace()
                    callback.registerResponse(null,false)
                }else{
                    callback.registerResponse(userInfo,true)
                }
            }
        }

        /**
         * return
         *  true -> 號碼已存在
         *  false -> 號碼不存在
         *  null -> 查詢失敗
         * */
        fun isUserRegister(phoneNumber: String,callback: FirebaseCallback?){
            val query = db.child("user").orderByChild("phone").equalTo(phoneNumber).ref
            query.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    callback?.isUserRegistered(phoneNumber,null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var existed : Boolean? = false
                    for(data in p0.children) {
                        existed = data.getValue(UserInfo::class.java)?.run {
                            phone == phoneNumber
                        }
                    }
                    callback?.isUserRegistered(phoneNumber, existed)
                }

            })
        }
    }
}
