package com.client.inningstempotracker.ui.analytics

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

data class InningsAnalytics(
    val match: MatchEntity,
    val overs: List<OverEntity>,
    val totalRuns: Int,
    val averagePerOver: Double,
    val totalWickets: Int,
    val bestOver: OverEntity?,
    val stabilityScore: String
)

sealed class AnalyticsState {
    object Loading : AnalyticsState()
    data class Success(
        val allMatches: List<MatchEntity>,
        val selectedAnalytics: List<InningsAnalytics>
    ) : AnalyticsState()
    data class Error(val message: String) : AnalyticsState()
}

class AnalyticsViewModel(
    private val matchRepository: MatchRepository,
    private val overRepository: OverRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AnalyticsState>(AnalyticsState.Loading)
    val state: StateFlow<AnalyticsState> = _state

    private val _selectedMatchIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedMatchIds: StateFlow<Set<Int>> = _selectedMatchIds

    private var allMatches: List<MatchEntity> = emptyList()

    init {
        loadMatches()
    }

    private fun loadMatches() {
        viewModelScope.launch {
            matchRepository.getAllMatches()
                .catch { e -> _state.value = AnalyticsState.Error(e.message ?: "Error") }
                .collect { matches ->
                    allMatches = matches
                    _state.value = AnalyticsState.Success(
                        allMatches = matches,
                        selectedAnalytics = emptyList()
                    )
                }
        }
    }

    fun toggleMatchSelection(matchId: Int) {
        val current = _selectedMatchIds.value.toMutableSet()
        if (current.contains(matchId)) {
            current.remove(matchId)
        } else {
            current.add(matchId)
        }
        _selectedMatchIds.value = current
        loadSelectedAnalytics()
    }

    private fun loadSelectedAnalytics() {
        viewModelScope.launch {
            val analytics = mutableListOf<InningsAnalytics>()
            _selectedMatchIds.value.forEach { matchId ->
                val match = matchRepository.getMatchById(matchId) ?: return@forEach
                val overs = overRepository.getOversByMatchIdOnce(matchId)
                val totalRuns = overs.sumOf { it.runs }
                val averagePerOver = if (overs.isEmpty()) 0.0
                else totalRuns.toDouble() / overs.size
                val totalWickets = overs.count { it.wicket }
                val bestOver = overs.maxByOrNull { it.runs }
                val stabilityScore = calculateStability(overs)
                analytics.add(
                    InningsAnalytics(
                        match = match,
                        overs = overs,
                        totalRuns = totalRuns,
                        averagePerOver = averagePerOver,
                        totalWickets = totalWickets,
                        bestOver = bestOver,
                        stabilityScore = stabilityScore
                    )
                )
            }
            _state.value = AnalyticsState.Success(
                allMatches = allMatches,
                selectedAnalytics = analytics
            )
        }
    }

    private fun calculateStability(overs: List<OverEntity>): String {
        if (overs.size < 2) return "N/A"
        val runs = overs.map { it.runs }
        val mean = runs.average()
        val variance = runs.map { (it - mean) * (it - mean) }.average()
        val stdDev = Math.sqrt(variance)
        return when {
            stdDev < 3.0 -> "High"
            stdDev < 6.0 -> "Medium"
            else -> "Low"
        }
    }
}