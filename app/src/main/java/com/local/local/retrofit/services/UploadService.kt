package com.local.local.retrofit.services

import com.local.local.retrofit.body.ImageUploadBody
import com.local.local.retrofit.body.ResponseBody
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.*


interface UploadService {
    @POST("3/upload")
    fun uploadImage(
        @Header("Authorization") authorization: String = "Client-ID 507d938c85f2ab1",
        @Body body: ImageUploadBody
    ):Deferred<Response<ResponseBody>>
}