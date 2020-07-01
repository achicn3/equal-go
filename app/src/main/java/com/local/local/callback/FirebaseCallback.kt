package com.local.local.callback

import com.local.local.body.*
import com.local.local.screen.fragment.ui.points.transaction.record.TransactionRecord

open class FirebaseCallback {
    open fun isPhoneExisted(phoneNumber: String?, response: Boolean?){}
    open fun registerResponse(userInfo: UserInfo?, response: Boolean?){} //true 註冊成功,false 註冊失敗
    open fun getUserInfoByPhone(userInfo: UserInfo?){}  //透過phoneNumber檢查使用者是否有註冊
    open fun getUserInfoByKey(userInfo: UserInfo?){} //透過userKey取得使用者資訊(qr code scan)
    open fun isKeyExisted(userKey: String?,response: Boolean?){}
    open fun addFriendResponse(response: Boolean?){}//true ->增加好友成功 false失敗
    open fun isFriendsAdded(response: Boolean?){} // true->已經加過好友了 false 還沒加過
    open fun retrieveFriendList(friendList: List<UserInfo>?){}//取得好友清單~
    open fun retrieveDefaultAvatar(avatarList: List<String?>){}//取得預設大頭貼
    open fun retrieveRecord(recordInfo: RecordInfo?){} //搜尋某天的紀錄
    open fun retrieveStatics(recordInfoList : List<RecordInfo?>){}//取得當月總數據
    open fun addStore(store: StoreInfo){}//商家註冊成功後發送商家資訊
    open fun retrieveStoreInfo(storeNames: List<StoreInfo>){}//取得所有商家的名稱
    open fun retrieveStoreItems(storeItems: List<StoreItems>){} //取得商家的優惠列表
    open fun updateUserInfoResponse(isSuccess : Boolean){}
    open fun userRetrieveTransactionRecord(transactionInfo: List<TransactionInfo>){}
    open fun storeRetrieveTransactionRecord(transactionInfo : List<TransactionInfo>){}
}