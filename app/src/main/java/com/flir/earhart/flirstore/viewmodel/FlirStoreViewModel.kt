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

    val apps = mutableStateListOf<AppInfo>()

    /** PackageName */
    val appsAddedToDownload = mutableStateListOf<String>()
    init {
        apps.clear()

        viewModelScope.launch {
            repository.downloadFlow().collect {
                appsAddedToDownload.clear()
                appsAddedToDownload.addAll(it)
            }
        }

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = application.packageManager

        repository.queryIntentActivities(pm, intent).forEach { resolveInfo ->

            val appInfo = AppInfo(
                name = resolveInfo.loadLabel(pm).toString(),
                icon = resolveInfo.activityInfo.loadIcon(pm).toBitmap(),
                packageName = resolveInfo.activityInfo.packageName,
                versionNum = "",
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        if(appsAddedToDownload.contains(it.packageName)){
                            repository.cancelDownload(it.packageName, InstallType.INSTALLATION)
                        } else {
                            repository.startDownload(it.packageName, InstallType.INSTALLATION)
                        }
                    }
                }
            )
            apps.add(appInfo)
        }
    }
}