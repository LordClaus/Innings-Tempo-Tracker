package com.client.inningstempotracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OverDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOver(over: OverEntity): Long

    @Update
    suspend fun updateOver(over: OverEntity)

    @Delete
    suspend fun deleteOver(over: OverEntity)

    @Query("SELECT * FROM overs WHERE matchId = :matchId ORDER BY overNumber ASC")
    fun getOversByMatchId(matchId: Int): Flow<List<OverEntity>>

    @Query("SELECT * FROM overs WHERE matchId = :matchId ORDER BY overNumber ASC")
    suspend fun getOversByMatchIdOnce(matchId: Int): List<OverEntity>

    @Query("SELECT * FROM overs WHERE id = :overId")
    suspend fun getOverById(overId: Int): OverEntity?

    @Query("DELETE FROM overs WHERE matchId = :matchId")
    suspend fun deleteOversByMatchId(matchId: Int)

    @Query("DELETE FROM overs")
    suspend fun deleteAllOvers()
}