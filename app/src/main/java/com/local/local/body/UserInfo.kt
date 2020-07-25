package com.local.local.body

import android.location.Location

/**
 * 使用者的資訊
 * */
data class UserInfo(
    var phone: String? = null,
    var name: String? = null,
    var sex : String? = null,
    var age: Int? = null,
    var freq : Int? = 0,
    var userKey : String? = null,
    var avatarUrl : String = "https://i.imgur.com/WUq3xlF.png",
    var points : Int = 0,
    var latitude : Double? = null,
    var longitude : Double? = null
){
    fun updateLocation(location: Location){
        latitude = location.latitude
        longitude = location.longitude
    }

    fun updatePoints(value: Int) : Int{
        points += value
        return points
    }
}