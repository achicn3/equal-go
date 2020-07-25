package com.local.local.body

/**
 * 管理員登入所需輸入資訊
 * */
data class AdminAccountBody(
    val email: String = "",
    val password: String = ""
)