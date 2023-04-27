package com.flir.earhart.flirstore.models

import com.squareup.moshi.Json

//[{"name":"app-debug.apk","url":"http://localhost:5000/download?apk=app-debug.apk"}]
data class ApkInfo(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)