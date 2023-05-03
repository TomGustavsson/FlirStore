package com.flir.earhart.flirstore.models

import android.graphics.Bitmap

data class AppInfo(
    val name: String? = null,
    val apkName: String,
    val downloadUrl: String,
    val alreadyInstalled: Boolean,
    val icon: Bitmap? = null,
    val packageName: String? = null,
    val versionNum: String? = null
)

enum class InstallType {
    UPDATE,
    INSTALLATION
}