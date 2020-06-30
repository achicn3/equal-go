package com.local.local.body

/**
 *  商家放置的商品
 * */
data class StoreItems(
        var description: String = "描述",
        var needPoints: Int = 0,
        var imgUrl: String ="https://i.imgur.com/WUq3xlF.png",
        var storeName : String = "商家名稱",
        var storeType : String = "商家類別"
)