package com.local.local.retrofit.body

import com.google.gson.annotations.SerializedName
import java.io.File

data class ImageUploadBody(
    @SerializedName("image") val image: String,
    @SerializedName("video") val video: File? = null,
    @SerializedName("album") val albumHash: String = "tNFVkUY",
    @SerializedName("type") val type: String = "base64",
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("disable_audio") val disableAudio: Boolean = true
)