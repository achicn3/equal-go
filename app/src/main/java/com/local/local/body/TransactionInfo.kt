package com.local.local.body

/**
 * 當使用者有兌換物品，此為兌換物品在資料庫的型態
 * 紀錄兌換的物品名稱、類別、當下剩餘點數、交易日期
 * */
data class TransactionInfo(
        var productName: String = "測試商家",
        var productType: String ="食",
        var year: String ="2020",
        var month: String ="01",
        var day: String ="01",
        var leftPoints: Int = 0,
        var storeKey: String = ""
)