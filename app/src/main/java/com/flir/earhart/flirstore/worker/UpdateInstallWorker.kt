package com.flir.earhart.flirstore.worker

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageInstaller.SessionParams
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flir.earhart.flirstore.DownloadApi
import com.flir.earhart.flirstore.models.InstallType
import com.flir.earhart.flirstore.service.CacheService
import org.koin.core.component.KoinComponent
import java.io.InputStream
import java.io.OutputStream


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
         //   val responseBody = api.downloadApk(it)
         //   val inputStream = responseBody.body()
          enqueueDownload(it)
        }
        //Fetch downloads from external storage and install.

        //Then install

        return Result.success()
    }

    private fun enqueueDownload(apkName: String) {
        //String url = "http://speedtest.ftp.otenet.gr/files/test10Mb.db";
        //String fileName = url.substring(url.lastIndexOf('/') + 1);
        //fileName = fileName.substring(0,1).toUpperCase() + fileName.substring(1);
        //File file = Util.createDocumentFile(fileName, context);
        var destination = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + apkName

        val request = DownloadManager.Request(Uri.parse("http://10.0.2.2:5000/download?apk=app-debug.apk"))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN) // Visibility of the download Notification
            .setDestinationUri(Uri.parse("$FILE_BASE_PATH$destination")) // Uri of the destination file
            .setTitle(apkName) // Title of the Download Notification
            .setDescription("Downloading") // Description of the Download Notification
            .setRequiresCharging(false) // Set if charging is required to begin the download
            .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
            .setAllowedOverRoaming(true) // Set if download is allowed on roaming network

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
