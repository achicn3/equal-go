package com.local.local.screen.fragment.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.util.FirebaseUtil
import java.io.File

class ProfileInfoViewModel : ViewModel() {
    var uploadFile : File? = null
    private val firebaseCallback = object : FirebaseCallback() {
        override fun retrieveDefaultAvatar(avatarList: List<String?>) {
            super.retrieveDefaultAvatar(avatarList)
            defaultAvatarList.value?.addAll(avatarList)
            defaultAvatarList.notifyObserver()
        }
    }
    val defaultAvatarList = MutableLiveData(mutableListOf<String?>())

    fun retrieveDefaultAvatar() = FirebaseUtil.retrieveDefaultAvatar(firebaseCallback)

}