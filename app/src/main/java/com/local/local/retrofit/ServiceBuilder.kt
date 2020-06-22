package com.local.local.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.local.local.retrofit.services.UploadService
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServiceBuilder {
    companion object{
        private const val imgurUploadUrl = "https://api.imgur.com/"

        const val DEFAULT_CONNECT_TIMEOUT_IN_SECOND = 30L
        const val DEFAULT_WRITE_TIMEOUT_IN_SECOND = 30L
        const val DEFAULT_READ_TIMEOUT_IN_SECOND = 30L

        @JvmOverloads
        fun buildOkHttpClient(
            connectTimeout: Long = DEFAULT_CONNECT_TIMEOUT_IN_SECOND,
            writeTimeout: Long = DEFAULT_WRITE_TIMEOUT_IN_SECOND,
            readTimeout: Long = DEFAULT_READ_TIMEOUT_IN_SECOND
        ): OkHttpClient {
            val specBuilder = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).also {
                it.tlsVersions(TlsVersion.TLS_1_2)
                it.cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                )
            }
            return OkHttpClient.Builder()
                .connectionSpecs(listOf(specBuilder.build(), ConnectionSpec.CLEARTEXT))
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .build()
        }

        private fun getRetrofit(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                .baseUrl(imgurUploadUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClient)
                .build()


        fun buildUploadService(okHttpClient: OkHttpClient): UploadService
                = getRetrofit(okHttpClient).create(UploadService::class.java)
    }

}