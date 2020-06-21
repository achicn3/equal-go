package com.local.local.callback

import com.local.local.body.UserInfo

open class FirebaseCallback {
    open fun isUserRegistered(phoneNumber: String?,response: Boolean?){}
    open fun registerResponse(userInfo: UserInfo?,response: Boolean?){} //true 註冊成功,false 註冊失敗
    open fun login(userInfo: UserInfo){}
}