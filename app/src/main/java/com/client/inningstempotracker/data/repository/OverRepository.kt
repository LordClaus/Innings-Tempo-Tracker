package com.client.inningstempotracker.data.repository

import com.client.inningstempotracker.data.db.OverDao
import com.client.inningstempotracker.data.db.OverEntity
import kotlinx.coroutines.flow.Flow

class OverRepository(private val overDao: OverDao) {

    fun getOversByMatchId(matchId: Int): Flow<List<OverEntity>> =
        overDao.getOversByMatchId(matchId)

    suspend fun getOversByMatchIdOnce(matchId: Int): List<OverEntity> =
        overDao.getOversByMatchIdOnce(matchId)

    suspend fun getOverById(overId: Int): OverEntity? =
        overDao.getOverById(overId)

    suspend fun insertOver(over: OverEntity): Long =
        overDao.insertOver(over)

    suspend fun updateOver(over: OverEntity) =
        overDao.updateOver(over)

    suspend fun deleteOver(over: OverEntity) =
        overDao.deleteOver(over)

    suspend fun deleteOversByMatchId(matchId: Int) =
        overDao.deleteOversByMatchId(matchId)

    suspend fun deleteAllOvers() =
        overDao.deleteAllOvers()
}