package com.flir.earhart.flirstore

import com.flir.earhart.flirstore.models.ApkInfo
import retrofit2.http.GET


interface DownloadApi {

    @GET("list/json")
    suspend fun getApkList(): List<ApkInfo>
}