package com.local.local.body

/**
 * 好友的資訊(搜尋or加入後查詢對方資料時會用到)
 * */
data class FriendsBody(
    var friendKey: String = "",
    var friendPhone: String = ""
)