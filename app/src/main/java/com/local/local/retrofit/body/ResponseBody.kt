package com.local.local.retrofit.body

import com.google.gson.annotations.SerializedName

data class ResponseBody(
    @SerializedName("data") val data: Data?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("status") val status: Int?
) {
    data class Data(
        @SerializedName("id") val id: String?,
        @SerializedName("title") val title: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("datetime") val datetime: Long?,
        @SerializedName("type") val type: String?,
        @SerializedName("animated") val animated: Boolean?,
        @SerializedName("width") val width: Int?,
        @SerializedName("height") val height: Int?,
        @SerializedName("size") val size: Int?,
        @SerializedName("views") val views: Int?,
        @SerializedName("bandwidth") val bandwidth: Int?,
        @SerializedName("vote") val vote: Int?,
        @SerializedName("favorite") val favorite: Boolean?,
        @SerializedName("nsfw")val nsfw: Boolean?,
        @SerializedName("section")val section: String?,
        @SerializedName("account_url")val accountUrl: String?,
        @SerializedName("is_ad")val isAd: Boolean?,
        @SerializedName("in_most_viral")val inMostViarl:Boolean?,
        @SerializedName("tags")val tags:List<String?>,
        @SerializedName("ad_type")val adType: Int?,
        @SerializedName("in_gallery")val inGallery :Boolean?,
        @SerializedName("deletehash")val deleteHash : String?,
        @SerializedName("name")val name: String,
        @SerializedName("link")val link: String
    )
}