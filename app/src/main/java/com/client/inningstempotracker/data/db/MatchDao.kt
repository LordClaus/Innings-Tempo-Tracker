package com.client.inningstempotracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Delete
    suspend fun deleteMatch(match: MatchEntity)

    @Query("SELECT * FROM matches ORDER BY createdAt DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches")
    suspend fun getAllMatchesOnce(): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: Int): MatchEntity?

    @Query("SELECT * FROM matches ORDER BY createdAt DESC LIMIT 5")
    fun getRecentMatches(): Flow<List<MatchEntity>>

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()

    @Query("SELECT COUNT(*) FROM matches")
    suspend fun getMatchCount(): Int
}