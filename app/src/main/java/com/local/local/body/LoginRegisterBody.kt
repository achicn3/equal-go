package com.local.local.body

data class LoginRegisterBody(
        var accountID : String = "",
        var pwd : String = "",
        var storeInfo: StoreInfo? = null
)