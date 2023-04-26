package com.flir.earhart.flirstore.service

import android.content.Context
import com.flir.earhart.flirstore.models.InstallType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

const val DEFAULT_GROUP_KEY = "installType"

class CacheService(private val context: Context) {

    private val _downloadFlow = MutableSharedFlow<List<String>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val lock = Mutex()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadScheduledForDownload()
        }
    }

    suspend fun downloadFlow(): Flow<List<String>> = _downloadFlow

    suspend fun saveDownload(packageName: String, type: InstallType) {
        // Save packageName to installation type.
        lock.withLock {
            val sharedPref = context.getSharedPreferences(DEFAULT_GROUP_KEY, Context.MODE_PRIVATE)

            val currentUpdates = getDownloads(type)
            val newUpdates = HashSet<String>(currentUpdates)
            newUpdates.add(packageName)

            with(sharedPref.edit()) {
                this.putStringSet(type.toString(), newUpdates)
                commit()
            }
            loadScheduledForDownload()
        }
    }

    private suspend fun getDownloads(type: InstallType): Set<String> {
        //Return either the ones queued for install or update
        val sharedPref = context.getSharedPreferences(DEFAULT_GROUP_KEY, Context.MODE_PRIVATE)
        return sharedPref.getStringSet(type.toString(), null) ?: emptySet()
    }

    suspend fun cancelDownload(packageName: String, type: InstallType) {
        lock.withLock {
            val sharedPref = context.getSharedPreferences(DEFAULT_GROUP_KEY, Context.MODE_PRIVATE)

            val currentUpdates = getDownloads(type) ?: emptySet()
            val newUpdates = HashSet<String>(currentUpdates)
            newUpdates.remove(packageName)

            with(sharedPref.edit()) {
                this.putStringSet(type.toString(), newUpdates)
                commit()
            }
            loadScheduledForDownload()
        }
    }

    private suspend fun loadScheduledForDownload() {
        /** We want all type of downloads */
        val downloads = getDownloads(InstallType.INSTALLATION) + getDownloads(InstallType.UPDATE)
        _downloadFlow.emit(downloads.toList())
    }

    companion object {
        private const val DOWNLOAD_LIST_KEY = "DownloadListKey"
    }
}