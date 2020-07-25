package com.local.local.body

/**
 * 商家註冊/登入的Body
 * */
data class StoreLoginRegisterBody(
        var accountID: String = "",
        var pwd: String = "",
        var storeInfo: StoreInfo? = null
)