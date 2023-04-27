package com.flir.earhart.flirstore.models

import android.graphics.Bitmap

data class AppInfo(
    val name: String,
    val downloadUrl: String,
    val alreadyInstalled: Boolean,
    val icon: Bitmap? = null,
    val packageName: String? = null,
    val versionNum: String? = null,
    val onClick: (AppInfo) -> Unit
)

enum class InstallType {
    UPDATE,
    INSTALLATION
}