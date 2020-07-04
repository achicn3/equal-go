package com.local.local.screen.store.items

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kdanmobile.cloud.event.EventManager
import com.local.local.body.StoreInfo
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
        class OnSaveStart : Event()
        class OnSaveFinish : Event()
        class OnSaveSuc : Event()
        class OnSaveFailed : Event()
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
        oldStoreItems: StoreItems? = null,
        adminPassStoreInfo: StoreInfo?= null
    ) {
        Event.OnSaveStart().send()
        //商家編輯商品並且有上傳新照片
        if( uploadFile != null && oldStoreItems != null) {
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
                        val storeInfo = StoreLoginManager.instance.storeInfo ?: adminPassStoreInfo ?: return@withContext
                        val itemsKey = FirebaseUtil.getKey() ?: return@withContext
                        val newStoreItems = StoreItems(
                            couponName,
                            needPoints,
                            response.body()?.data?.link ?: oldStoreItems.imgUrl,
                            storeInfo.storeName,
                            storeInfo.storeType,
                            oldStoreItems.storeItemsKey
                        )
                        FirebaseUtil.storeAddCoupon(
                            storeInfo,
                            newStoreItems,
                            firebaseCallback
                        )
                    } else {
                        Event.OnSaveFinish().send()
                        Event.OnSaveFailed().send()
                    }
                }
            }
        }
        else if (uploadFile == null && oldStoreItems != null) {
            val storeInfo = StoreLoginManager.instance.storeInfo ?: adminPassStoreInfo ?: return
            val newStoreItems = StoreItems(
                couponName,
                needPoints,
                oldStoreItems.imgUrl,
                storeInfo.storeName,
                storeInfo.storeType,
                oldStoreItems.storeItemsKey
            )
            FirebaseUtil.storeAddCoupon(
                storeInfo,
                newStoreItems,
                firebaseCallback
            )
            return
        } else if (uploadFile != null && oldStoreItems == null) {
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
                        val storeInfo = StoreLoginManager.instance.storeInfo ?: adminPassStoreInfo ?: return@withContext
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
                        FirebaseUtil.storeAddCoupon(storeInfo, storeItems, firebaseCallback)
                    } else {
                        Event.OnSaveFinish().send()
                        Event.OnSaveFailed().send()
                    }
                }
            }
        }
    }
}