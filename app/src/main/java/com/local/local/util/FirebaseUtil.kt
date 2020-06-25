package com.local.local.util

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.manager.LoginManager

class FirebaseUtil {
    companion object{
        private val db = FirebaseDatabase.getInstance().reference
        private const val USER_NODE = "user"
        private const val DISTANCE_NODE = "distance"
        private fun isUniversalPhoneNumber(phoneNumber: String?): Boolean =
            phoneNumber?.substring(0, 4) == "+886"

        private fun toUniversalPhoneNumber(phoneNumber: String?): String =
            "+886${phoneNumber?.substring(1)}"

        data class DistanceDataClass(var moveDistance: Float = 0f)

        fun updateMoveDistance(moveDistance: Float, date: String) {
            val dateFormat = date.split("/")
            val key = LoginManager.instance.userData?.userKey
            var oldDistance: Float = 0f
            key?.run {
                //先檢查有沒有先前的移動距離，有的話要取出加回。
                db.child(USER_NODE).child(key).child(DISTANCE_NODE).child(dateFormat[0]).child(dateFormat[1]).child(dateFormat[2])
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                p0.toException().printStackTrace()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val oldDistanceValue = p0.getValue(DistanceDataClass::class.java)
                                Log.d("status","oldDistancevalue : $oldDistanceValue or value : ${p0.value}")
                                oldDistance = oldDistanceValue?.moveDistance?.plus(moveDistance)
                                        ?: moveDistance
                                Log.d("value", "after update: $oldDistance")
                                val updateDistance = DistanceDataClass(oldDistance)
                                Log.d("status", "hhere rere $updateDistance")
                                db.child(USER_NODE).child(key).child(DISTANCE_NODE).child(dateFormat[0]).child(dateFormat[1]).child(dateFormat[2]).setValue(updateDistance)
                            }
                        })
            }
        }

        fun updateUserInfo() {
            val key = LoginManager.instance.userData?.userKey
            key?.run {
                db.child(USER_NODE).child(this).setValue(
                        LoginManager.instance.userData
                ) { p0, p1 ->
                    p0?.toException()?.printStackTrace()
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
            db.child(USER_NODE).child(key).child("friends").addListenerForSingleValueEvent(object : ValueEventListener {
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
                    db.child("user").child(it).child("friends").orderByChild("phone").equalTo(userInfo?.phone).ref
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
                                Log.d("stauts","in checking phone ${userInfo?.phone}")
                                Log.d("status","in checking ${userInfo?.phone == v?.phone} $v")
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
                db.child(USER_NODE).child(it).child("friends").push().setValue(userInfo) { p0, p1 ->
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
                ) { p0, p1 ->
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
