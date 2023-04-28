package com.flir.earhart.flirstore

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.WorkerParameters
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.flir.earhart.flirstore.repositories.FlirStoreRepository
import com.flir.earhart.flirstore.service.CacheService
import com.flir.earhart.flirstore.viewmodel.FlirStoreViewModel
import com.flir.earhart.flirstore.worker.UpdateInstallWorker
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class FlirStoreApplication: Application(), KoinComponent {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

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
                            FlirStoreRepository(get(), get(), get())
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
                                workerParams = workerParams,
                                cache = get(),
                                api = get()
                            )
                        }
                    },
                    module {
                        single {
                            Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(MoshiConverterFactory.create(get()))
                                .client(
                                    OkHttpClient.Builder()
                                        .addNetworkInterceptor(StethoInterceptor())
                                        .connectTimeout(60, TimeUnit.SECONDS)
                                        .readTimeout(30, TimeUnit.SECONDS)
                                        .writeTimeout(15, TimeUnit.SECONDS)
                                        .build()
                                )
                                .build()
                        }
                    },
                    module {
                        single {
                            get<Retrofit>().create(DownloadApi::class.java)
                        }
                    },
                    module {
                        single {
                            Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                        }
                    }
                )
            )
        }
    }
    companion object {
        private const val BASE_URL = "http://10.0.2.2:5000/"
    }
}