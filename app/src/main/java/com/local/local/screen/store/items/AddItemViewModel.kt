package com.local.local.screen.store.items

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.StoreItems
import com.local.local.callback.FirebaseCallback
import com.local.local.event.EventBroadcaster
import com.local.local.manager.StoreLoginManager
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

class AddItemViewModel(private val eventManager: EventManager<Event> = EventManager()) :
    ViewModel(), EventBroadcaster<AddItemViewModel.Event> by eventManager {
    sealed class Event {
        class OnSaveStart() : Event()
        class OnSaveFinish() : Event()
        class OnSaveSuc() : Event()
        class OnSaveFailed() : Event()
    }

    private val imageUploadServiceHolder by inject(ImageUploadServiceHolder::class.java)

    private fun Event.send() {
        eventManager.send(this)
    }

    private val firebaseCallback = object : FirebaseCallback() {
        override fun storeAddItemsResponse(response: Boolean) {
            Log.d("status", "store add response called...")
            Event.OnSaveFinish().send()
            when(response){
                true -> Event.OnSaveSuc().send()
                false -> Event.OnSaveFailed().send()
            }
        }
    }

    fun onClickConfirm(
        uploadFile: File?,
        couponName: String,
        needPoints: Int,
        oldStoreItems: StoreItems? = null
    ) {
        Event.OnSaveStart().send()
        if (uploadFile == null && oldStoreItems != null) {
            val storeInfo = StoreLoginManager.instance.storeInfo ?: return
            val itemsKey = FirebaseUtil.getKey() ?: return
            val newStoreItems = StoreItems(
                couponName,
                needPoints,
                oldStoreItems.imgUrl,
                storeInfo.storeName,
                storeInfo.storeType,
                itemsKey
            )
            FirebaseUtil.storeAddCoupon(
                storeInfo,
                newStoreItems,
                oldStoreItems.storeItemsKey,
                firebaseCallback
            )
            return
        } else if (uploadFile != null) {
            GlobalScope.launch(Dispatchers.IO) {
                val response = imageUploadServiceHolder.uploadImageAsync(
                    image = MultipartBody.Part.createFormData(
                        "image",
                        uploadFile.name,
                        uploadFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    ),
                    title = "test",
                    description = "avatar"
                ).await()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val storeInfo = StoreLoginManager.instance.storeInfo ?: return@withContext
                        val itemsKey = FirebaseUtil.getKey() ?: return@withContext
                        val link = response.body()?.data?.link ?: return@withContext
                        val storeItems = StoreItems(
                            couponName,
                            needPoints,
                            link,
                            storeInfo.storeName,
                            storeInfo.storeType,
                            itemsKey
                        )
                        FirebaseUtil.storeAddCoupon(storeInfo, storeItems, null, firebaseCallback)
                    } else {
                        Event.OnSaveFinish().send()
                        Event.OnSaveFailed().send()
                    }
                }
            }
        }
    }
}