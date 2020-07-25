package com.local.local.screen.user.ui.firends.friendlsit

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.body.FriendsBody
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.util.FirebaseUtil

class FriendListViewModel(private val context: Context) : ViewModel() {
    val friendKeyListImp = MutableLiveData(mutableListOf<FriendsBody>())
    val friendKeyList: LiveData<MutableList<FriendsBody>> = friendKeyListImp
    private val friendListImp = MutableLiveData(mutableListOf<UserInfo?>())
    val friendList: LiveData<MutableList<UserInfo?>> = friendListImp

    private val firebaseCallback = object : FirebaseCallback() {
        override fun retrieveFriendList(friendList: List<FriendsBody>?) {
            friendList?.also { list ->
                friendKeyListImp.value?.clear()
                friendKeyListImp.value?.addAll(list)
                friendKeyListImp.notifyObserver()
            }
        }

        override fun getUserInfoByKey(userInfo: UserInfo?) {
            friendListImp.value?.add(userInfo)
            friendListImp.notifyObserver()
        }
    }

    fun retrieveFriendList(){
        FirebaseUtil.retrieveFriendList(firebaseCallback)
    }

    fun searchUserInfoByKey(userKey: String){
        FirebaseUtil.getUserInfoByKey(userKey,firebaseCallback)
    }

}