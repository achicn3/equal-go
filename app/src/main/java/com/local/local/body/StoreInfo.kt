package com.local.local.body

import java.io.Serializable

/**
 * 商家登入後取得的資訊
 * */
data class StoreInfo(
        var storeName: String ="",
        var storeType: String ="",
        var key: String =""
) : Serializable