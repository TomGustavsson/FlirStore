package com.flir.earhart.flirstore

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.dsl.module

class FlirStoreApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FlirStoreApplication)
            modules(
                module {
                    viewModelOf(::FlirStoreViewModel)
                }
            )
        }
    }
}