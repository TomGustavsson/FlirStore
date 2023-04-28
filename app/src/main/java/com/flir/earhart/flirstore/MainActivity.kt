package com.flir.earhart.flirstore

import android.Manifest
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageInstaller
import android.content.pm.PackageInstaller.SessionParams
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
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
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream


class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<FlirStoreViewModel>()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        permissionCheck()
        super.onCreate(savedInstanceState)
        setContent {
            FlirStoreTheme {
                // A surface container using the 'background' color from the theme
                AppListScreen(viewModel) {
                    enqueueDownload(it.name)
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

    private fun enqueueDownload(apkName: String) {
        /** Download apk using DownloadManager */
        val destination = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + apkName
        val uri = Uri.parse("$FILE_BASE_PATH$destination")
        val file = File(destination)
        if (file.exists()) file.delete()

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse("http://10.0.2.2:5000/download?apk=app-debug.apk")
        val request = DownloadManager.Request(downloadUri)
            .setMimeType(MIME_TYPE)
            .setTitle(this.getString(R.string.title_file_download))
            .setDescription(this.getString(R.string.downloading))
            .setDestinationUri(uri)

        downloadManager.enqueue(request)

        /** Add broadcast receiver that listens for download completion */
        showInstallOption(destination, this)
    }

    private fun showInstallOption(
        destination: String,
        context: Context
    ) {
        // set BroadcastReceiver to install app when .apk is downloaded
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                val contentUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + PROVIDER_PATH,
                    File(destination)
                )
                val install = Intent(Intent.ACTION_VIEW)
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                install.data = contentUri
                context.startActivity(install)
                context.unregisterReceiver(this)
            }
        }
        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


 /*   fun installPackage(context: Context, `in`: InputStream, packageName: String?): Boolean {
        val packageInstaller = context.packageManager.packageInstaller
        val params = SessionParams(
            SessionParams.MODE_FULL_INSTALL
        )
        params.setAppPackageName(packageName)
        // set params
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)
        val out: OutputStream = session.openWrite("COSU", 0, -1)
        val buffer = ByteArray(65536)
        var c: Int
        while (`in`.read(buffer).also { c = it } != -1) {
            out.write(buffer, 0, c)
        }
        session.fsync(out)
        `in`.close()
        out.close()

        session.commit(PendingIntent.getBroadcast(
            context,
            sessionId,
            Intent(Intent.ACTION_INSTALL_PACKAGE),
            0).intentSender
        )
        return true
    } */
  /*  fun installPackage(
        context: Context
    ): Boolean {
        val pm = context.packageManager

        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val session = pm.packageInstaller.createSession(params)

        val out = session.openWrite("my_app", 0, -1)
        val inStream = FileInputStream(File(apkFilePath))

        val buffer = ByteArray(65536)
        var c: Int
        while (inStream.read(buffer).also { c = it } != -1) {
            out.write(buffer, 0, c)
        }

        session.commit(
            PendingIntent.getBroadcast(
                context,
                session.sessionId,
                Intent(Intent.ACTION_INSTALL_PACKAGE),
                0
            ).intentSender
        )

        out.close()
        inStream.close()
    } */
    companion object {
        private const val FILE_NAME = "SampleDownloadApp.apk"
        private const val FILE_BASE_PATH = "file://"
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val PROVIDER_PATH = ".provider"
        private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
    }
}

private const val TAG = "AppInstaller"
