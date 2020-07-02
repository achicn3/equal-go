package com.local.local.manager

import com.local.local.body.StoreInfo

class StoreLoginManager private constructor(){
    companion object{
        val instance = Holder.INSTANCE
    }
    object Holder{
        val INSTANCE = StoreLoginManager()
    }
    var storeInfo : StoreInfo? = null


}