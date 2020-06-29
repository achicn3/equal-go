package com.local.local.util

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.local.local.body.RecordInfo
import com.local.local.body.StoreInfo
import com.local.local.body.TransactionItems
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.manager.LoginManager

@Suppress("NAME_SHADOWING")
class FirebaseUtil {
    companion object{
        private val db = FirebaseDatabase.getInstance().reference
        private const val USER_NODE = "user"
        private const val DISTANCE_NODE = "distance"
        private const val RECORD_NODE = "record"
        private const val FRIENDS_NODE = "friends"
        private const val STORE_NODE = "stores"
        private const val EXCHANGE_NODE = "exchange"
        private fun isUniversalPhoneNumber(phoneNumber: String?): Boolean =
                phoneNumber?.substring(0, 4) == "+886"

        private fun toUniversalPhoneNumber(phoneNumber: String?): String =
                "+886${phoneNumber?.substring(1)}"



        fun retrieveStoreInfo(firebaseCallback: FirebaseCallback){
            db.child(STORE_NODE).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.retrieveStoreInfo(listOf())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val storeInfo = mutableListOf<StoreInfo>()
                    for(data in p0.children){
                        val info = data.getValue(StoreInfo::class.java) ?: continue
                        storeInfo.add(info)
                    }
                    firebaseCallback.retrieveStoreInfo(storeInfo)
                }

            })
        }

        fun retrieveStoreItems(storeKey: String, firebaseCallback: FirebaseCallback){
            db.child(EXCHANGE_NODE).child(storeKey).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.retrieveStoreItems(listOf())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val itemsList = arrayListOf<TransactionItems>()
                    for(data in p0.children){
                        val items = data.getValue(TransactionItems::class.java) ?: continue
                        itemsList.add(items)
                    }
                    firebaseCallback.retrieveStoreItems(itemsList)
                }
            })
        }

        fun retrieveStatics(year: Int,Month:Int,firebaseCallback: FirebaseCallback){
            val key = LoginManager.instance.userData?.userKey
            val monthStr = if(Month<10) "0$Month" else "$Month"
            key?.run {
                db.child(RECORD_NODE).child(this).child(year.toString()).child(monthStr).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        p0.toException().printStackTrace()
                        firebaseCallback.retrieveStatics(listOf())
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val list = mutableListOf<RecordInfo?>()
                        for(data in p0.children){
                            val value = data.getValue(RecordInfo::class.java)
                            value?.days = data.key?.toInt()
                            Log.d("status","statics value :$value")
                            list.add(value)
                        }
                        firebaseCallback.retrieveStatics(list.toList())
                    }

                })
            }
        }

        fun retrieveRecord(date: String, firebaseCallback: FirebaseCallback) {
            val key = LoginManager.instance.userData?.userKey
            val dateFormat = date.split("/")
            Log.d("status","date format : $dateFormat key : $key")
            key?.run {
                db.child(RECORD_NODE).child(this).child(dateFormat[0]).child(dateFormat[1]).child(dateFormat[2]).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        p0.toException().printStackTrace()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val value = p0.getValue(RecordInfo::class.java)
                        Log.d("status","info $value")
                        firebaseCallback.retrieveRecord(value)
                    }

                })
            }
        }

        fun updateRecord(date: String, distance: Float, points: Int) {
            val key = LoginManager.instance.userData?.userKey
            val dateFormat = date.split("/")
            key?.run {
                db.child(RECORD_NODE).child(key).child(dateFormat[0]).child(dateFormat[1]).child(dateFormat[2]).apply {
                    addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            p0.toException().printStackTrace()
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val value = p0.getValue(RecordInfo::class.java) ?: RecordInfo(0f, 0)
                            value.distance += distance
                            value.points += points
                            setValue(value)
                        }
                    })
                }
            }
        }

        fun updateUserInfo(firebaseCallback: FirebaseCallback? = null) {
            val key = LoginManager.instance.userData?.userKey
            key?.run {
                db.child(USER_NODE).child(this).setValue(
                        LoginManager.instance.userData
                ) { p0, _ ->
                    firebaseCallback?.updateUserInfoResponse(p0 == null)
                }
            }
        }

        fun retrieveDefaultAvatar(firebaseCallback: FirebaseCallback){
            db.child("avatar").child("default").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.retrieveDefaultAvatar(listOf<String>())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val list = mutableListOf<String>()
                    for(data in p0.children){
                        val url = data?.value?.toString() ?: continue
                        list.add(url)
                    }
                    firebaseCallback.retrieveDefaultAvatar(list.toList())
                }

            })
        }

        fun retrieveFriendList(firebaseCallback: FirebaseCallback){
            val key = LoginManager.instance.userData?.userKey ?: return
            db.child(FRIENDS_NODE).child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.retrieveFriendList(null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val friendList = mutableListOf<UserInfo>()
                    for(data in p0.children){
                        val friendInfo = data.getValue(UserInfo::class.java) ?: continue
                        friendList.add(friendInfo)
                    }
                    firebaseCallback.retrieveFriendList(friendList.toList())
                }

            })
        }

        fun getUserInfoByKey(userKey: String?, callback: FirebaseCallback) {
            val query = db.child(USER_NODE).orderByChild("userKey").equalTo(userKey).ref
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    callback.getUserInfoByKey(null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (data in p0.children) {
                        val userInfo = data.getValue(UserInfo::class.java)
                        if (userInfo?.userKey == userKey) {
                            callback.getUserInfoByKey(userInfo)
                            break
                        }
                    }
                }

            })
        }

        fun checkFriendsAlreadyAdd(userInfo: UserInfo?, callback: FirebaseCallback){
            val userKey = LoginManager.instance.userData?.userKey
            userKey?.let {
                run{
                    db.child(FRIENDS_NODE).child(it).orderByChild("phone").equalTo(userInfo?.phone).ref
                }.also {
                    it.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            p0.toException().printStackTrace()
                            callback.isFriendsAdded(null)
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            var existed : Boolean? = false
                            for(data in p0.children){
                                val v = data.getValue(UserInfo::class.java)
                                existed = data.getValue(UserInfo::class.java)?.run {
                                    phone == userInfo?.phone
                                }
                                if(existed != null && existed)break
                            }
                            callback.isFriendsAdded(existed)
                        }

                    })
                }

            }
        }

        fun addFriends(userInfo: UserInfo?, callback: FirebaseCallback) {
            val userKey = LoginManager.instance.userData?.userKey
            userKey?.let {
                db.child(FRIENDS_NODE).child(it).push().setValue(userInfo) { p0, _ ->
                    if (p0 != null) {
                        callback.addFriendResponse(false)
                    } else {
                        callback.addFriendResponse(true)
                    }
                }
            }
        }

        fun getUserInfoByPhone(phoneNumber: String?, callback: FirebaseCallback) {
            val phoneNumber = if (!isUniversalPhoneNumber(phoneNumber)) {
                toUniversalPhoneNumber(phoneNumber)
            } else {
                phoneNumber
            }
            val query = db.child(USER_NODE).orderByChild("phone").equalTo(phoneNumber).ref
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    callback.getUserInfoByPhone(null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (data in p0.children) {
                        val userInfo = data.getValue(UserInfo::class.java)
                        if (userInfo?.phone == phoneNumber) {
                            callback.getUserInfoByPhone(userInfo)
                            break
                        }
                    }
                }

            })
        }

        fun sendUserToServer(userInfo: UserInfo, callback: FirebaseCallback){
            run {
                db.child(USER_NODE).push().key
            }?.also { key ->
                userInfo.userKey = key
                db.child(USER_NODE).child(key).setValue(
                        userInfo
                ) { p0, _ ->
                    if (p0 != null) {
                        p0.toException().printStackTrace()
                        callback.registerResponse(null, false)
                    } else {
                        callback.registerResponse(userInfo, true)
                    }
                }
            }
        }

        fun isKeyExisted(scannedKey: String?, callback: FirebaseCallback?) {
            val query = db.child(USER_NODE).orderByChild("userKey").equalTo(scannedKey).ref
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    callback?.isKeyExisted(scannedKey, null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var existed: Boolean? = false
                    for (data in p0.children) {
                        existed = data.getValue(UserInfo::class.java)?.run {
                            userKey == scannedKey
                        }
                        if (existed != null && existed) break
                    }
                    callback?.isKeyExisted(scannedKey, existed)
                }

            })
        }

        /**
         * return
         *  true -> 號碼已存在
         *  false -> 號碼不存在
         *  null -> 查詢失敗
         * */
        fun isPhoneExisted(phoneNumber: String, callback: FirebaseCallback) {
            val phoneNumber = if (!isUniversalPhoneNumber(phoneNumber)) {
                toUniversalPhoneNumber(phoneNumber)
            } else {
                phoneNumber
            }
            val query = db.child(USER_NODE).orderByChild("phone").equalTo(phoneNumber).ref
            query.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    callback.isPhoneExisted(phoneNumber, null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var existed : Boolean? = false
                    for(data in p0.children) {
                        Log.d(
                                "in searching",
                                "search ... userPhone = $phoneNumber ${data.getValue(UserInfo::class.java)}"
                        )
                        existed = data.getValue(UserInfo::class.java)?.run {
                            phone == phoneNumber
                        }
                        if (existed != null && existed) break
                    }
                    callback.isPhoneExisted(phoneNumber, existed)
                }

            })
        }
    }
}
