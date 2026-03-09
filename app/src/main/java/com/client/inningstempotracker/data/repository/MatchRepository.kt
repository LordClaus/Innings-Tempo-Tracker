package com.client.inningstempotracker.data.repository

import com.client.inningstempotracker.data.db.MatchDao
import com.client.inningstempotracker.data.db.MatchEntity
import kotlinx.coroutines.flow.Flow

class MatchRepository(private val matchDao: MatchDao) {

    fun getAllMatches(): Flow<List<MatchEntity>> = matchDao.getAllMatches()

    fun getRecentMatches(): Flow<List<MatchEntity>> = matchDao.getRecentMatches()

    suspend fun getMatchById(matchId: Int): MatchEntity? = matchDao.getMatchById(matchId)

    suspend fun insertMatch(match: MatchEntity): Long = matchDao.insertMatch(match)

    suspend fun updateMatch(match: MatchEntity) = matchDao.updateMatch(match)

    suspend fun deleteMatch(match: MatchEntity) = matchDao.deleteMatch(match)

    suspend fun deleteAllMatches() = matchDao.deleteAllMatches()

    suspend fun getMatchCount(): Int = matchDao.getMatchCount()
}