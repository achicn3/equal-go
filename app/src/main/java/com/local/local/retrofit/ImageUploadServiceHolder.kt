package com.local.local.retrofit

import com.local.local.retrofit.body.ImageUploadBody
import com.local.local.retrofit.body.ResponseBody
import com.local.local.retrofit.services.ServiceBuilder
import com.local.local.retrofit.services.UploadService
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Response

class ImageUploadServiceHolder(private val okHttpClient: OkHttpClient) : UploadService {
    private val service: UploadService = ServiceBuilder.buildUploadService(okHttpClient)
    override fun uploadImageAsync(
        authorization: String,
        body: ImageUploadBody
    ): Deferred<Response<ResponseBody>> = service.uploadImageAsync(body = body)
}