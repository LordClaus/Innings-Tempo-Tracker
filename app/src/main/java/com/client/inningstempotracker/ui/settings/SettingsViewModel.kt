package com.client.inningstempotracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.client.inningstempotracker.data.datastore.PreferencesManager
import com.client.inningstempotracker.data.repository.MatchRepository
import com.client.inningstempotracker.data.repository.OverRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SettingsState {
    object Idle : SettingsState()
    object Loading : SettingsState()
    data class Success(val message: String) : SettingsState()
    data class Error(val message: String) : SettingsState()
}

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val matchRepository: MatchRepository,
    private val overRepository: OverRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val state: StateFlow<SettingsState> = _state

    fun clearLibrary() {
        viewModelScope.launch {
            _state.value = SettingsState.Loading
            try {
                matchRepository.deleteAllMatches()
                overRepository.deleteAllOvers()
                _state.value = SettingsState.Success("Library cleared successfully")
            } catch (e: Exception) {
                _state.value = SettingsState.Error(e.message ?: "Failed to clear library")
            }
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            _state.value = SettingsState.Loading
            try {
                preferencesManager.resetAllPreferences()
                _state.value = SettingsState.Success("Settings reset successfully")
            } catch (e: Exception) {
                _state.value = SettingsState.Error(e.message ?: "Failed to reset settings")
            }
        }
    }

    fun resetState() {
        _state.value = SettingsState.Idle
    }
}