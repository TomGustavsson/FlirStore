package com.flir.earhart.flirstore

import android.Manifest
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.flir.earhart.flirstore.composables.AppListScreen
import com.flir.earhart.flirstore.ui.theme.FlirStoreTheme
import com.flir.earhart.flirstore.viewmodel.FlirStoreViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<FlirStoreViewModel>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        permissionCheck()
        super.onCreate(savedInstanceState)
        setContent {
            FlirStoreTheme {
                // A surface container using the 'background' color from the theme
                AppListScreen(viewModel) { appInfo ->
                    if(viewModel.queuedDownloads.contains(appInfo.apkName)) {
                        viewModel.queuedDownloads[appInfo.apkName]?.let {
                            cancelDownload(appInfo.apkName, it)
                        }
                    } else {
                        enqueueDownload(appInfo.apkName)
                    }
                }
            }
        }
    }

    private fun permissionCheck() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun cancelDownload(apkName: String, id: Long) {
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.remove(id)
        viewModel.removeFromDownloadQueue(apkName)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun enqueueDownload(apkName: String) {
        /** Download apk using DownloadManager */
        val destination = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + apkName
        val uri = Uri.parse("$FILE_BASE_PATH$destination")
        val file = File(destination)
         if(file.exists()) {
             file.delete()
        }
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse("$DOWNLOAD_ROOT_PATH$apkName")
        val request = DownloadManager.Request(downloadUri)
            .setMimeType(MIME_TYPE)
            .setTitle(this.getString(R.string.title_file_download))
            .setDescription(this.getString(R.string.downloading))
            .setDestinationUri(uri)

        val id = downloadManager.enqueue(request)
        viewModel.addToDownloadQueue(apkName, id)
        /** Add broadcast receiver that listens for download completion */
        showInstallOption(destination, apkName)
    }


    private fun showInstallOption(
        destination: String,
        apkName: String
    ) {
        // set BroadcastReceiver to install app when .apk is downloaded
        val onComplete = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                if(File(destination).exists()) {
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + PROVIDER_PATH,
                        File(destination)
                    )
                    val install = Intent(Intent.ACTION_VIEW)
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    install.data = contentUri
                    startActivity(install)
                    unregisterReceiver(this)
                    viewModel.removeFromDownloadQueue(apkName)
                }
            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        viewModel.refreshAppList()
    }

    companion object {
        private const val FILE_BASE_PATH = "file://"
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val PROVIDER_PATH = ".provider"
        private const val DOWNLOAD_ROOT_PATH = "http://10.0.2.2:5000/download?apk="
    }
}
