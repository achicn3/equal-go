package com.local.local.body

import java.io.Serializable

//TODO : 商家註冊後發送的資訊
data class StoreInfo(
        var storeName: String ="",
        var storeType: String ="",
        var key: String =""
) : Serializable