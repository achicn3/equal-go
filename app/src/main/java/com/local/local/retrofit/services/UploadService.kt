package com.local.local.retrofit.services

import com.local.local.retrofit.body.ResponseBody
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File


interface UploadService {
    @POST("3/upload")
    fun uploadImageAsync(
        @Header("Authorization") authorization: String = "Client-ID 507d938c85f2ab1",
        @Part image: MultipartBody.Part,
        @Query("video")  video: File? = null,
        @Query("album")  albumHash: String? = null,
        @Query("type")  type: String = "file",
        @Query("title")  title: String = "avatar",
        @Query("description")  description: String = "avatar",
        @Query("disable_audio")  disableAudio: Boolean = true
    ): Deferred<Response<ResponseBody>>
}