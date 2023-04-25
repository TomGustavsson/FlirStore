package com.flir.earhart.flirstore.models

import android.graphics.Bitmap

data class AppInfo(
    val name: String,
    val icon: Bitmap,
    val packageName: String,
    val onClick: (AppInfo) -> Unit
)