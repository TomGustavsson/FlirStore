package com.flir.earhart.flirstore.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent

class UpdateInstallWorker(
    context: Context,
    workerParams: WorkerParameters,
): CoroutineWorker(context, workerParams), KoinComponent {

    override suspend fun doWork(): Result {
        //Start upload..
        //val getAllUpdates = cache.getDownloads(type)
        return Result.success()
    }

}
