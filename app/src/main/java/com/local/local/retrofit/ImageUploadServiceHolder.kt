package com.local.local.retrofit

import com.local.local.retrofit.body.ResponseBody
import com.local.local.retrofit.services.ServiceBuilder
import com.local.local.retrofit.services.UploadService
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import java.io.File

class ImageUploadServiceHolder(private val okHttpClient: OkHttpClient) : UploadService {
    private val service: UploadService = ServiceBuilder.buildUploadService(okHttpClient)

    override fun uploadImageAsync(
        authorization: String,
        image: MultipartBody.Part,
        video: File?,
        albumHash: String?,
        type: String,
        name: String,
        title: String,
        description: String,
        disableAudio: Boolean
    ): Deferred<Response<ResponseBody>> =
        service.uploadImageAsync(
            authorization, image, video, albumHash, type, name, title, description
        )
}
/**
 * Upload example
 *
 *
GlobalScope.launch(Dispatchers.IO) {
    val response = holder.uploadImageAsync(
    image = MultipartBody.Part.createFormData(
    "image",
    fileName,
    uploadFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
    ),
    name = fileName,
    title = "test",
    description = "avatar"
    ).await()
    withContext(Dispatchers.Main) {
        val body = response.body()
        val errorBody = response.errorBody()?.string()?.toString()
        Log.d(
        "status",
        "body: ${GsonBuilder().setPrettyPrinting().create()
        .toJson(body)},error Body:${GsonBuilder().setPrettyPrinting().create()
        .toJson(errorBody)}"
    )
    }
}

 *
 * */