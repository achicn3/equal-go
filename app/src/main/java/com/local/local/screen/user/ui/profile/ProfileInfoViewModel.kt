package com.local.local.screen.user.ui.profile

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.local.local.callback.FirebaseCallback
import com.local.local.extensions.Extensions.notifyObserver
import com.local.local.manager.UserLoginManager
import com.local.local.retrofit.ImageUploadServiceHolder
import com.local.local.util.FirebaseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.java.KoinJavaComponent.inject
import java.io.File

class ProfileInfoViewModel : ViewModel() {
    var uploadFile : File? = null
    private val imageUploadServiceHolder by inject(ImageUploadServiceHolder::class.java)
    private val firebaseCallback = object : FirebaseCallback() {
        override fun retrieveDefaultAvatar(avatarList: List<String?>) {
            super.retrieveDefaultAvatar(avatarList)
            defaultAvatarList.value?.addAll(avatarList)
            defaultAvatarList.notifyObserver()
        }
    }
    val defaultAvatarList = MutableLiveData(mutableListOf<String?>())

    fun retrieveDefaultAvatar() = FirebaseUtil.retrieveDefaultAvatar(firebaseCallback)

    fun onClickSave(userClickPosition: Int, name: String) {
        var changed = false
        if (!TextUtils.isEmpty(name)) {
            UserLoginManager.instance.userData?.name = name
            UserLoginManager.instance.alertUserInfoChanged()
            FirebaseUtil.updateUserInfo()
        }
        if (userClickPosition != -1) {
            val newUrl = defaultAvatarList.value?.get(userClickPosition) ?: return
            UserLoginManager.instance.userData?.avatarUrl = newUrl
            UserLoginManager.instance.alertUserInfoChanged()
            FirebaseUtil.updateUserInfo()
        } else if (uploadFile != null) {
            GlobalScope.launch(Dispatchers.IO) {
                val response = imageUploadServiceHolder.uploadImageAsync(
                        image = MultipartBody.Part.createFormData(
                                "image",
                                uploadFile?.name,
                                uploadFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        ),
                        title = "test",
                        description = "avatar"
                ).await()
                withContext(Dispatchers.Main) {
                    val body = response.body()
                    val errorBody = response.errorBody()?.string()
                    Log.d(
                            "status",
                            "body: ${GsonBuilder().setPrettyPrinting().create()
                                    .toJson(body)},error Body:${GsonBuilder().setPrettyPrinting().create()
                                    .toJson(errorBody)}"
                    )
                    if (response.isSuccessful) {
                        UserLoginManager.instance.userData?.avatarUrl = response.body()?.data?.link
                                ?: return@withContext
                        changed = true
                        UserLoginManager.instance.alertUserInfoChanged()
                        FirebaseUtil.updateUserInfo()
                    }
                }
            }
        }
    }
}