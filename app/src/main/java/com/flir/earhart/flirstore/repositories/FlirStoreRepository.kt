package com.flir.earhart.flirstore.repositories

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.util.Log
import androidx.work.*
import com.flir.earhart.flirstore.DownloadApi
import com.flir.earhart.flirstore.models.ApkInfo
import com.flir.earhart.flirstore.models.InstallType
import com.flir.earhart.flirstore.service.CacheService
import com.flir.earhart.flirstore.worker.UpdateInstallWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.TimeUnit

class FlirStoreRepository(
    private val application: Application,
    private val cache: CacheService,
    private val api: DownloadApi,
) {

    suspend fun getApkList(): List<ApkInfo> {
        return api.getApkList()
    }

    @Suppress("DEPRECATION")
    fun queryIntentActivities(pm: PackageManager, intent: Intent): List<ResolveInfo> {
        return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, 0)
        } else {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags
                .of(PackageManager.MATCH_ALL.toLong()))
        }
    }

    suspend fun cancelDownload(packageName: String, type: InstallType) {
        cache.cancelDownload(packageName, type)
    }

    suspend fun downloadFlow(): Flow<List<String>> = cache.downloadFlow()

    suspend fun startDownload(
        apkName: String,
        type: InstallType
    ) {
        Log.d(TAG, "Added to download queue: $apkName")
        cache.saveDownload(apkName, type)

        val work = OneTimeWorkRequest.Builder(UpdateInstallWorker::class.java)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setInitialDelay(5000, TimeUnit.MILLISECONDS) //Debounce
            .build()

        WorkManager.getInstance(application).beginUniqueWork(UPDATE_INSTALL_WORK, ExistingWorkPolicy.REPLACE, work)
            .enqueue()
    }

    companion object {
        private const val TAG = "FlirStoreRepository"
        private const val UPDATE_INSTALL_WORK = "UpdateInstallWorker"
    }
}