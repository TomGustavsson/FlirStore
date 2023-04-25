package com.flir.earhart.flirstore

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import com.flir.earhart.flirstore.models.AppInfo

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class FlirStoreViewModel(
    private val application: Application
): ViewModel() {

    val apps = mutableStateListOf<AppInfo>()

    init {
        apps.clear()

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = application.packageManager

        queryIntentActivities(pm, intent).forEach { resolveInfo ->

            Log.d("tgiw", resolveInfo.activityInfo.toString())
           // val versionName = pm.getPackageInfo(resolveInfo.activityInfo.packageName, PackageManager.PackageInfoFlags.of(PackageManager.MATCH_ALL.toLong())).versionName
            val appInfo = AppInfo(
                name = resolveInfo.loadLabel(pm).toString(),
                icon = resolveInfo.activityInfo.loadIcon(pm).toBitmap(),
                packageName = resolveInfo.activityInfo.packageName,
                versionNum = "",//versionName,
                onClick = {
                }
            )
            apps.add(appInfo)
        }
    }

    @Suppress("DEPRECATION")
    private fun queryIntentActivities(pm: PackageManager, intent: Intent): List<ResolveInfo> {
        return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, 0)
        } else {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags
                .of(PackageManager.MATCH_ALL.toLong()))
        }
    }

}