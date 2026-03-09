package com.client.inningstempotracker

import android.app.Application
import com.client.inningstempotracker.di.appModule
import com.client.inningstempotracker.di.databaseModule
import com.client.inningstempotracker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class InningsTempoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@InningsTempoApp)
            modules(
                appModule,
                databaseModule,
                viewModelModule
            )
        }
    }
}