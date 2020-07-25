package com.local.local.callback

import com.local.local.body.*

open class FirebaseCallback {
    open fun isPhoneExisted(phoneNumber: String?, response: Boolean?) {}//檢查該手機號碼是否存在
    open fun registerResponse(userInfo: UserInfo?, response: Boolean?) {} //true 註冊成功,false 註冊失敗
    open fun getUserInfoByPhone(userInfo: UserInfo?){}  //透過phoneNumber檢查使用者是否有註冊
    open fun getUserInfoByKey(userInfo: UserInfo?) {} //透過userKey取得使用者資訊(qr code scan)
    open fun isKeyExisted(userKey: String?, response: Boolean?) {}
    open fun addFriendResponse(response: Boolean?) {}//true ->增加好友成功 false失敗
    open fun isFriendsAdded(response: Boolean?) {} // true->已經加過好友了 false 還沒加過
    open fun retrieveFriendList(friendList: List<FriendsBody>?) {}//取得好友清單~
    open fun retrieveDefaultAvatar(avatarList: List<String?>) {}//取得預設大頭貼
    open fun retrieveRecord(recordInfo: RecordInfo?) {} //搜尋某天的紀錄
    open fun retrieveStatics(recordInfoList: List<RecordInfo?>) {}//取得當月總數據
    open fun retrieveStoreInfo(storeNames: List<StoreInfo>) {}//取得所有商家的名稱
    open fun retrieveStoreItems(storeItems: List<StoreItems>) {} //取得商家的優惠列表
    open fun updateUserInfoResponse(isSuccess: Boolean) {}//更新使用者資訊的回應
    open fun userRetrieveTransactionRecord(transactionInfo: List<TransactionInfo>) {}//使用者取得交易紀錄
    open fun storeRetrieveTransactionRecord(transactionInfo: List<StoreTransactionRecordBody>) {}//商家取得交易紀錄
    open fun storeCheckRegistered(registered: Boolean) {}//檢查該商家是否已註冊
    open fun storeSendRegisterInfoResponse(response: Boolean) {}//取得商家註冊後的回應
    open fun storeLoginResponse(
        response: Boolean,
        bodyStore: StoreLoginRegisterBody?
    ) {
    }//取得商家登入後的回應

    open fun storeAddItemsResponse(response: Boolean) {}//商家增加商品之Response , true= 成功,false = 失敗
    open fun storeCheckWaitingConfirm(response: Boolean) {}//檢查商家是否等待審核中
    open fun storeRemoveCouponResponse(response: Boolean) {}//商家移除優惠券的伺服器回應
    open fun adminLoginResponse(response: Boolean) {}//管理員登入回應
    open fun adminRetrieveUserList(userList: ArrayList<UserInfo>) {}//管理員取得使用者列表
    open fun adminRetrieveVerificationStore(storeList: ArrayList<StoreLoginRegisterBody>) {}//管理員審核商家
}