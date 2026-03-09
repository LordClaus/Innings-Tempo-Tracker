package com.client.inningstempotracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.client.inningstempotracker.data.db.MatchEntity
import com.client.inningstempotracker.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val recentMatches: List<MatchEntity>) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state

    init {
        loadRecentMatches()
    }

    private fun loadRecentMatches() {
        viewModelScope.launch {
            matchRepository.getRecentMatches()
                .catch { e ->
                    _state.value = HomeState.Error(e.message ?: "Unknown error")
                }
                .collect { matches ->
                    _state.value = HomeState.Success(matches)
                }
        }
    }
}