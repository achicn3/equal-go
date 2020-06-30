package com.local.local.body

/**
 * 當使用者有兌換物品，此為兌換物品在資料庫的型態
 * 紀錄兌換的物品名稱、類別、當下剩餘點數、交易日期
 * */
data class TransactionInfo(
        val productName: String,
        val productType: String,
        val year: String,
        val month: String,
        val day: String,
        val leftPoints: Int
)