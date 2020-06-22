package com.local.local.callback

import com.local.local.body.UserInfo

open class FirebaseCallback {
    open fun isPhoneExisted(phoneNumber: String?, response: Boolean?){}
    open fun registerResponse(userInfo: UserInfo?, response: Boolean?){} //true 註冊成功,false 註冊失敗
    open fun getUserInfoByPhone(userInfo: UserInfo?){}
    open fun getUserInfoByKey(userInfo: UserInfo?){} //透過userKey檢查使用者是否有註冊
    open fun isKeyExisted(userKey: String?,response: Boolean?){}
    open fun addFriendResponse(response: Boolean?){}//true ->增加好友成功 false失敗
    open fun isFriendsAdded(response: Boolean?){} // true->已經加過好友了 false 還沒加過
}