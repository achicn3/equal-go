package com.local.local.screen.fragment.ui.firends.friendlsit

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.util.FirebaseUtil

class FriendListViewModel(private val context: Context) : ViewModel() {
    val friendListImp = MutableLiveData(mutableListOf<UserInfo>())
    val friendList : LiveData<MutableList<UserInfo>> = friendListImp

    private fun <T> MutableLiveData<T>.notifyObserver(){
        this.value = this.value
    }

    private val firebaseCallback = object : FirebaseCallback(){
        override fun retrieveFriendList(friendList: List<UserInfo>?) {
            super.retrieveFriendList(friendList)
            friendList?.also { list ->
                friendListImp.value?.addAll(list)
                friendListImp.notifyObserver()
            }

        }
    }

    fun retrieveFriendList(){
        FirebaseUtil.retrieveFriendList(firebaseCallback)
    }

}