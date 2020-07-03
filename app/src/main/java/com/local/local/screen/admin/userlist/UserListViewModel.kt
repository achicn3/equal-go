package com.local.local.screen.admin.userlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.body.UserInfo
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.util.FirebaseUtil

class UserListViewModel : ViewModel() {
    val userList = MutableLiveData(arrayListOf<UserInfo>())

    init {
        val firebaseCallback = object : FirebaseCallback(){
            override fun adminRetrieveUserList(userList: ArrayList<UserInfo>) {
                this@UserListViewModel.userList.value?.clear()
                this@UserListViewModel.userList.value?.addAll(userList)
                this@UserListViewModel.userList.notifyObserver()
            }
        }
        retrieveUserList(firebaseCallback)
    }



    private fun retrieveUserList(firebaseCallback : FirebaseCallback){
        FirebaseUtil.adminRetrieveUserInfo(firebaseCallback)
    }

}