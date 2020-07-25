package com.local.local.body

/**
 * 商家檢視紀錄時的資訊
 * */
data class StoreTransactionRecordBody(
    var userAvatarUrl: String ="",
    var userName : String = "",
    var userPhone : String = "",
    var itemDescription : String = ""
)