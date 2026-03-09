package com.client.inningstempotracker.di

import com.client.inningstempotracker.data.datastore.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { PreferencesManager(androidContext()) }
}