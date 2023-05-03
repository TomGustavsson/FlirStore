package com.flir.earhart.flirstore.viewmodel

import android.app.Application
import android.content.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flir.earhart.flirstore.models.AppInfo
import com.flir.earhart.flirstore.repositories.FlirStoreRepository
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class FlirStoreViewModel(
    private val application: Application,
    private val repository: FlirStoreRepository
): ViewModel() {

    /** PackageName, will be used later when we do silent downloads */
    val appsAddedToDownload = mutableStateListOf<Pair<String, Long>>()

    val queuedDownloads = mutableStateMapOf<String, Long>()

    val availableApks = mutableStateListOf<AppInfo>()
    init {
        /** In this POC everything is handled in viewModel / MainActivity
         *  save cache, repository, api, worker and flows for future.
         */

       /* viewModelScope.launch {
            repository.downloadFlow().collect {
                appsAddedToDownload.clear()
                appsAddedToDownload.addAll(it)
            }
        } */
    }

     fun refreshAppList() {
         val intent = Intent(Intent.ACTION_MAIN, null)
         intent.addCategory(Intent.CATEGORY_LAUNCHER)
         val pm = application.packageManager
         viewModelScope.launch {

             val installedApks = repository.queryIntentActivities(application.packageManager, intent)

             availableApks.clear()
             repository.getApkList().map { apk ->
                /** Will always fail.. need more information in API call (apps name) */
                val alreadyInstalled = installedApks.firstOrNull { it.activityInfo.packageName == apk.name.removeSuffix(".apk") }

                availableApks.add(
                    AppInfo(
                        name = if(alreadyInstalled != null) alreadyInstalled.activityInfo?.loadLabel(pm).toString() else null,
                        apkName = apk.name,
                        downloadUrl = apk.url,
                        alreadyInstalled = alreadyInstalled != null,
                        icon = alreadyInstalled?.activityInfo?.loadIcon(pm)?.toBitmap(),
                        packageName = alreadyInstalled?.activityInfo?.packageName,
                        versionNum = if(alreadyInstalled != null) "Version 20.0.5" else null //Version check is not supported yet.
                    )
                )
            }
        }
    }
    fun addToDownloadQueue(apkName: String, id: Long) {
        queuedDownloads[apkName] = id
    }
    fun removeFromDownloadQueue(apkName: String) {
        queuedDownloads.remove(apkName)
    }
}
