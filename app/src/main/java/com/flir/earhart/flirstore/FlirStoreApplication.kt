package com.flir.earhart.flirstore

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.WorkerParameters
import com.flir.earhart.flirstore.repositories.FlirStoreRepository
import com.flir.earhart.flirstore.service.CacheService
import com.flir.earhart.flirstore.viewmodel.FlirStoreViewModel
import com.flir.earhart.flirstore.worker.UpdateInstallWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class FlirStoreApplication: Application(), KoinComponent {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FlirStoreApplication)
            workManagerFactory()
            modules(
                listOf(
                    module {
                        viewModelOf(::FlirStoreViewModel)
                    },
                    module {
                        single {
                            FlirStoreRepository(get(), get())
                        }
                    },
                    module {
                        single {
                            CacheService(get())
                        }
                    },
                    module {
                        worker {(workerParams: WorkerParameters) ->
                            UpdateInstallWorker(
                                context = get(),
                                workerParams = workerParams
                            )
                        }
                    }
                )
            )
        }
    }
}