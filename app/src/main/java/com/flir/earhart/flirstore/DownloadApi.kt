package com.flir.earhart.flirstore

import com.flir.earhart.flirstore.models.ApkInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

//[{"name":"app-debug.apk","url":"http://localhost:5000/download?apk=app-debug.apk"}]
interface DownloadApi {

    @GET("list/json")
    suspend fun getApkList(): List<ApkInfo>

    @GET("download")
    @Streaming
    suspend fun downloadApk(@Query("apk") apk: String): Response<ResponseBody>

    //@Headers(CustomCacheHeader.DEFAULT)
    //    @GET("/api/account/accounts/self")
    //    suspend fun getMyAccount(@Query("accountType") accountType: String): LambdaAccount
}