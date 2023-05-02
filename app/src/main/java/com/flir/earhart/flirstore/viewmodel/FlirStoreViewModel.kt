package com.flir.earhart.flirstore.viewmodel

import android.app.Application
import android.content.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flir.earhart.flirstore.models.AppInfo
import com.flir.earhart.flirstore.repositories.FlirStoreRepository
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class FlirStoreViewModel(
    application: Application,
    private val repository: FlirStoreRepository
): ViewModel() {
    /** PackageName */
    val appsAddedToDownload = mutableStateListOf<String>()

    val availableApks = mutableStateListOf<AppInfo>()
    init {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = application.packageManager

        viewModelScope.launch {
            val installedApks = repository.queryIntentActivities(pm, intent)
            repository.getApkList().map { apk ->
                /** Will always fail.. need more information in API call (apps name) */

                val alreadyInstalled = installedApks.firstOrNull { it.activityInfo.packageName== apk.name.removeSuffix(".apk") }

                availableApks.add(
                    AppInfo(
                        name = apk.name,
                        downloadUrl = apk.url,
                        alreadyInstalled = alreadyInstalled != null,
                        icon = alreadyInstalled?.activityInfo?.loadIcon(pm)?.toBitmap(),
                        packageName = alreadyInstalled?.activityInfo?.packageName,
                        versionNum = "Version 20.0.5" //Version check is not supported yet.
                  )
              )
            }
        }

        viewModelScope.launch {
            repository.downloadFlow().collect {
                appsAddedToDownload.clear()
                appsAddedToDownload.addAll(it)
            }
        }
    }
}
