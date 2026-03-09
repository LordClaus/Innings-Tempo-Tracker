package com.client.inningstempotracker.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.client.inningstempotracker.data.db.MatchEntity
import com.client.inningstempotracker.data.db.OverEntity
import com.client.inningstempotracker.data.repository.MatchRepository
import com.client.inningstempotracker.data.repository.OverRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class InningsDetailState {
    object Loading : InningsDetailState()
    data class Success(
        val match: MatchEntity,
        val overs: List<OverEntity>,
        val totalRuns: Int,
        val averagePerOver: Double,
        val totalWickets: Int,
        val bestOver: OverEntity?
    ) : InningsDetailState()
    data class Error(val message: String) : InningsDetailState()
}

class InningsDetailViewModel(
    private val matchRepository: MatchRepository,
    private val overRepository: OverRepository
) : ViewModel() {

    private val _state = MutableStateFlow<InningsDetailState>(InningsDetailState.Loading)
    val state: StateFlow<InningsDetailState> = _state

    fun init(matchId: Int) {
        viewModelScope.launch {
            val match = matchRepository.getMatchById(matchId)
            if (match == null) {
                _state.value = InningsDetailState.Error("Match not found")
                return@launch
            }
            overRepository.getOversByMatchId(matchId)
                .catch { e -> _state.value = InningsDetailState.Error(e.message ?: "Error") }
                .collect { overs ->
                    val totalRuns = overs.sumOf { it.runs }
                    val averagePerOver = if (overs.isEmpty()) 0.0
                    else totalRuns.toDouble() / overs.size
                    val totalWickets = overs.count { it.wicket }
                    val bestOver = overs.maxByOrNull { it.runs }
                    _state.value = InningsDetailState.Success(
                        match = match,
                        overs = overs,
                        totalRuns = totalRuns,
                        averagePerOver = averagePerOver,
                        totalWickets = totalWickets,
                        bestOver = bestOver
                    )
                }
        }
    }
}