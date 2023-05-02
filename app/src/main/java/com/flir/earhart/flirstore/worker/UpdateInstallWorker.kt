package com.flir.earhart.flirstore.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flir.earhart.flirstore.DownloadApi
import com.flir.earhart.flirstore.models.InstallType
import com.flir.earhart.flirstore.service.CacheService
import org.koin.core.component.KoinComponent


class UpdateInstallWorker(
    val context: Context,
    workerParams: WorkerParameters,
    private val cache: CacheService,
    private val api: DownloadApi
): CoroutineWorker(context, workerParams), KoinComponent {

    override suspend fun doWork(): Result {
        //Start upload..'
        val allDownloads = cache.getDownloads(InstallType.INSTALLATION)
        Log.d(TAG, "download started")
        allDownloads.forEach {
         // enqueueDownload(it)
        }
        return Result.success()
    }


    companion object {
            private const val FILE_NAME = "SampleDownloadApp.apk"
            private const val FILE_BASE_PATH = "file://"
            private const val MIME_TYPE = "application/vnd.android.package-archive"
            private const val PROVIDER_PATH = ".provider"
            private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""

        private const val TAG = "UpdateInstallWorker"
    }
}
