package com.local.local.body

data class UserInfo(
    var phone: String? = null,
    var name: String? = null,
    var sex : String? = null,
    var age: Int? = null,
    var freq : Int? = 0,
    var userKey : String? = null,
    var avatarUrl : String = "https://i.imgur.com/WUq3xlF.png"
)