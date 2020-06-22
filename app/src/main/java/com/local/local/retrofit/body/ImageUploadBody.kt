package com.local.local.retrofit.body

import retrofit2.http.Query
import java.io.File

data class ImageUploadBody(
    //BASE 64 image
    //If base 64 is invalid try this :
    //https://github.com/Killmax/imgur-upload-retrofit-2/blob/master/app/src/main/java/xyz/maxime_brgt/testretrofit/ImgurService.java
    @Query("image") val image: String,
    @Query("video") val video: File? = null,
    @Query("album") val albumHash: String = "tNFVkUY",
    @Query("type") val type: String = "base64",
    @Query("name") val name: String,
    @Query("title") val title: String,
    @Query("description") val description: String,
    @Query("disable_audio") val disableAudio: Boolean = true
)