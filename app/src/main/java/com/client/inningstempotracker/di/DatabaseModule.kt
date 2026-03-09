package com.client.inningstempotracker.di

import androidx.room.Room
import com.client.inningstempotracker.data.db.AppDatabase
import com.client.inningstempotracker.data.repository.MatchRepository
import com.client.inningstempotracker.data.repository.OverRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    single { get<AppDatabase>().matchDao() }
    single { get<AppDatabase>().overDao() }

    single { MatchRepository(get()) }
    single { OverRepository(get()) }
}