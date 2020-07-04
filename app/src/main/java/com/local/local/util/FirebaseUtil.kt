package com.local.local.util

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.local.local.body.*
import com.local.local.callback.FirebaseCallback
import com.local.local.manager.UserLoginManager

@Suppress("NAME_SHADOWING")
class FirebaseUtil {
    companion object{
        private val db = FirebaseDatabase.getInstance().reference
        private const val USER_NODE = "user"
        private const val DISTANCE_NODE = "distance"
        private const val RECORD_NODE = "record"
        private const val FRIENDS_NODE = "friends"
        private const val STORE_NODE = "stores" //使用者用來拿商店資訊的
        private const val EXCHANGE_NODE = "exchange"    //儲存商家的商品節點
        private const val TRANSACTION_RECORD_INFO = "transaction_record" //使用者儲存交易紀錄的節點
        private const val STORE_EXCHANGE_RECORD_INFO = "store_transaction_record"   //商家用來抓取被兌換商品的節點
        private const val STORE_REGISTER_SEND_TO_ADMIN = "store_register_wait_verification" //等待審核的商家節點
        private const val STORE_USER_NODE = "store_user"    //儲存商店使用者的資訊節點
        private const val ADMIN_USER_NODE = "admin"
        private fun isUniversalPhoneNumber(phoneNumber: String?): Boolean =
                phoneNumber?.substring(0, 4) == "+886"

        private fun toUniversalPhoneNumber(phoneNumber: String?): String =
                "+886${phoneNumber?.substring(1)}"

        fun adminLogin(accountID: String, pwd: String, firebaseCallback: FirebaseCallback) {
            db.child(ADMIN_USER_NODE).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.adminLoginResponse(false)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var suc = false
                    for (data in p0.children) {
                        val value = data.getValue(AdminAccountBody::class.java) ?: continue
                        if (value.email == accountID && value.password == pwd) {
                            suc = true
                            break
                        }
                    }
                    firebaseCallback.adminLoginResponse(suc)
                }

            })
        }

        fun adminRetrieveUserInfo(firebaseCallback: FirebaseCallback) {
            db.child(USER_NODE).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.adminRetrieveUserList(arrayListOf())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val userList = arrayListOf<UserInfo>()
                    for (data in p0.children) {
                        val userInfo = data.getValue(UserInfo::class.java) ?: continue
                        userList.add(userInfo)
                    }
                    firebaseCallback.adminRetrieveUserList(userList)
                }

            })
        }

        fun adminRetrieveVerificationStore(firebaseCallback: FirebaseCallback) {
            db.child(STORE_REGISTER_SEND_TO_ADMIN)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        p0.toException().printStackTrace()
                        firebaseCallback.adminRetrieveVerificationStore(arrayListOf())
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val storeList = arrayListOf<LoginRegisterBody>()
                        for (ch in p0.children) {
                            val data = ch.getValue(LoginRegisterBody::class.java) ?: continue
                            storeList.add(data)
                        }
                        firebaseCallback.adminRetrieveVerificationStore(storeList)
                    }

                })
        }

        fun adminConfirmStoreInfo(store: LoginRegisterBody) {
            val key = store.storeInfo?.key ?: return
            db.child(STORE_REGISTER_SEND_TO_ADMIN).child(key).apply {
                addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        p0.toException().printStackTrace()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val data = p0.getValue(LoginRegisterBody::class.java)
                        db.child(STORE_NODE).child(key).setValue(store.storeInfo)
                        db.child(STORE_USER_NODE).child(key).setValue(data)
                        removeValue()
                    }

                })
            }
        }

        fun storeAddCoupon(
            storeInfo: StoreInfo,
            storeItems: StoreItems,
            firebaseCallback: FirebaseCallback
        ) {
                db.child(EXCHANGE_NODE).child(storeInfo.key).child(storeItems.storeItemsKey)
                    .setValue(storeItems) { p0, p1 ->
                        firebaseCallback.storeAddItemsResponse(p0 == null)
                        Log.d("status","hasdhadshasdh is succ? ${p0==null}")
                    }

        }

        fun storeCheckIfWaitingConfirm(accountID: String,firebaseCallback: FirebaseCallback){
            db.child(STORE_REGISTER_SEND_TO_ADMIN).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.storeCheckWaitingConfirm(true)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var isWaiting = false
                    for(data in p0.children){
                        val value = data.getValue(LoginRegisterBody::class.java) ?: continue
                        if(value.accountID == accountID){
                            isWaiting = true
                            break
                        }
                    }
                    firebaseCallback.storeCheckWaitingConfirm(isWaiting)
                }

            })
        }

        fun storeLogin(accountID: String,pwd: String,firebaseCallback: FirebaseCallback){
            db.child(STORE_USER_NODE).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.storeLoginResponse(false,null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var response = false
                    var body : LoginRegisterBody? = null
                    for(ch in p0.children){
                        body = ch.getValue(LoginRegisterBody::class.java) ?: continue
                        if(body.accountID == accountID && body.pwd == pwd){
                            response = true
                            break
                        }
                    }
                    firebaseCallback.storeLoginResponse(response,body)
                }

            })
        }

        fun storeCheckIfExisted(accountID: String, firebaseCallback: FirebaseCallback){
            db.child(STORE_USER_NODE).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.storeCheckRegistered(false)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var existed = false
                    for(children in p0.children){
                        val data = children.getValue(LoginRegisterBody::class.java) ?: continue
                        if(data.accountID == accountID){
                            existed = true
                            break
                        }
                    }
                    firebaseCallback.storeCheckRegistered(existed)
                }
            })
        }

        fun getKey() : String? {
            return db.child("dummy").push().key
        }

        fun storeSendRegisterInfoToAdmin(accountID: String,pwd: String,storeInfo: StoreInfo,firebaseCallback: FirebaseCallback){
            val account = LoginRegisterBody(accountID, pwd,storeInfo)
            db.child(STORE_REGISTER_SEND_TO_ADMIN).child(storeInfo.key).setValue(account){ p0,_ ->
                firebaseCallback.storeSendRegisterInfoResponse(p0 == null)
            }
        }

        fun storeRetrieveTransactionInfo(
            storeInfo: StoreInfo,
            year: String,
            month: String,
            firebaseCallback: FirebaseCallback
        ) {
            val key = storeInfo.key
            db.child(STORE_EXCHANGE_RECORD_INFO)
                    .child(key)
                    .child(year)
                    .child(month)
                .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            p0.toException().printStackTrace()
                            firebaseCallback.storeRetrieveTransactionRecord(listOf())
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val records = mutableListOf<StoreTransactionRecordBody>()
                            for(data in p0.children){
                                for (items in data.children) {
                                    val record =
                                        items.getValue(StoreTransactionRecordBody::class.java)
                                            ?: continue
                                    records.add(record)
                                }
                            }
                            firebaseCallback.storeRetrieveTransactionRecord(records)
                        }
                    })
        }

        fun userRetrieveTransactionInfo(year: String,month: String,day: String,firebaseCallback: FirebaseCallback){
            val key = UserLoginManager.instance.userData?.userKey
            key?.run {
                db.child(TRANSACTION_RECORD_INFO)
                        .child(this)
                        .child(year)
                        .child(month)
                        .child(day)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                                p0.toException().printStackTrace()
                                firebaseCallback.userRetrieveTransactionRecord(listOf())
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val records = mutableListOf<TransactionInfo>()
                                for(data in p0.children){
                                    val transactionInfo = data.getValue(TransactionInfo::class.java) ?: continue
                                    records.add(transactionInfo)
                                }
                                firebaseCallback.userRetrieveTransactionRecord(records)
                            }
                        })
            }
        }

        /**
         * 使用者兌換物品成功，儲存交易紀錄。
         * */
        fun addTransactionInfo(transactionInfo: TransactionInfo,storeInfo: StoreInfo){
            val userData = UserLoginManager.instance.userData ?: return
            val userName = userData.name ?: return
            val userPhone = userData.phone ?: return
            val transactionRecordBody = StoreTransactionRecordBody(
                userData.avatarUrl,
                userName,
                userPhone,
                transactionInfo.productDescription
            )
            val key = userData.userKey
            key?.run {
                val year= transactionInfo.year
                val month = transactionInfo.month
                val day = transactionInfo.day
                db.child(TRANSACTION_RECORD_INFO)
                        .child(this)
                        .child(year)
                        .child(month)
                        .child(day)
                        .push()
                        .setValue(transactionInfo){ p0,p1 ->
                        }
                db.child(STORE_EXCHANGE_RECORD_INFO)
                        .child(storeInfo.key)
                        .child(year)
                        .child(month)
                        .child(day)
                        .push()
                    .setValue(transactionRecordBody)
            }
        }

        fun retrieveStoreInfo(firebaseCallback: FirebaseCallback){
            db.child(STORE_NODE).addValueEventListener(object : ValueEventListener{
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

        fun retrieveStoreItems(storeInfo: StoreInfo, firebaseCallback: FirebaseCallback){
            val storeKey = storeInfo.key
            Log.d("status","in retrieveStoreItems the storekey is $storeKey")
            db.child(EXCHANGE_NODE).child(storeKey)
                .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.retrieveStoreItems(listOf())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val itemsList = arrayListOf<StoreItems>()
                    for(data in p0.children){
                        Log.d("status","in retrieveStoreItems the storeItems is ${data.getValue(StoreItems::class.java)}")
                        val items = data.getValue(StoreItems::class.java) ?: continue
                        itemsList.add(items)
                    }
                    firebaseCallback.retrieveStoreItems(itemsList)
                }
            })
        }

        fun retrieveStatics(year: Int,Month:Int,firebaseCallback: FirebaseCallback){
            val key = UserLoginManager.instance.userData?.userKey
            val monthStr = if(Month<10) "0$Month" else "$Month"
            key?.run {
                db.child(RECORD_NODE).child(this).child(year.toString()).child(monthStr).addValueEventListener(object: ValueEventListener{
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
            val key = UserLoginManager.instance.userData?.userKey
            val dateFormat = date.split("/")
            key?.run {
                db.child(RECORD_NODE).child(this).child(dateFormat[0]).child(dateFormat[1]).child(dateFormat[2]).addValueEventListener(object : ValueEventListener {
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
            val key = UserLoginManager.instance.userData?.userKey
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
            val key = UserLoginManager.instance.userData?.userKey
            key?.run {
                db.child(USER_NODE).child(this).setValue(
                        UserLoginManager.instance.userData
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
            val key = UserLoginManager.instance.userData?.userKey ?: return
            db.child(FRIENDS_NODE).child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                    firebaseCallback.retrieveFriendList(null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val friendList = mutableListOf<AddFriendsBody>()
                    for(data in p0.children){
                        val friendInfo = data.getValue(AddFriendsBody::class.java) ?: continue
                        Log.d("status","friend Info is ${friendInfo}")
                        friendList.add(friendInfo)
                    }
                    firebaseCallback.retrieveFriendList(friendList.toList())
                }

            })
        }

        fun getUserInfoByKey(userKey: String?, callback: FirebaseCallback) {
            val query = db.child(USER_NODE).orderByChild("userKey").equalTo(userKey).ref
            query.addValueEventListener(object : ValueEventListener {
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
            val userKey = UserLoginManager.instance.userData?.userKey
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
                                existed = data.getValue(AddFriendsBody::class.java)?.run {
                                    friendPhone == userInfo?.phone
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
            val userKey = UserLoginManager.instance.userData?.userKey
            userKey?.let {
                val friendsBody = AddFriendsBody(userInfo?.userKey!!,userInfo.phone!!)
                db.child(FRIENDS_NODE).child(it).push().setValue(friendsBody) { p0, _ ->
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
