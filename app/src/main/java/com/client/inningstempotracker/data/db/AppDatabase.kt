package com.client.inningstempotracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MatchEntity::class, OverEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
    abstract fun overDao(): OverDao

    companion object {
        const val DATABASE_NAME = "innings_tempo_tracker.db"
    }
}