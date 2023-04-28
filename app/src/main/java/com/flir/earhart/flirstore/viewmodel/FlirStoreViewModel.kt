package com.flir.earhart.flirstore.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flir.earhart.flirstore.repositories.FlirStoreRepository
import com.flir.earhart.flirstore.models.AppInfo
import com.flir.earhart.flirstore.models.InstallType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class FlirStoreViewModel(
    private val application: Application,
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

                val alreadyInstalled = installedApks.firstOrNull { it.activityInfo.name == apk.name }

                availableApks.add(
                    AppInfo(
                        name = apk.name,
                        downloadUrl = apk.url,
                        alreadyInstalled = alreadyInstalled != null,
                        icon = alreadyInstalled?.activityInfo?.loadIcon(pm)?.toBitmap(),
                        packageName = alreadyInstalled?.activityInfo?.packageName,
                        versionNum = "Version 20.0.5",
                        onClick = {
                            /* viewModelScope.launch {
                                repository.startDownload(apk.name, InstallType.INSTALLATION)
                            } */
                        }
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