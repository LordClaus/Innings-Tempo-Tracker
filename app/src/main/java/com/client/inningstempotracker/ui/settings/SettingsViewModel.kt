package com.client.inningstempotracker.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.client.inningstempotracker.data.datastore.PreferencesManager
import com.client.inningstempotracker.data.repository.MatchRepository
import com.client.inningstempotracker.data.repository.OverRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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

    val isDarkTheme: StateFlow<Boolean> = preferencesManager.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkTheme(isDark)
        }
    }

    fun exportData(context: Context, uri: Uri) {
        viewModelScope.launch {
            _state.value = SettingsState.Loading
            try {
                val matches = matchRepository.getAllMatchesOnce()
                val json = buildString {
                    append("{\"matches\":[")
                    matches.forEachIndexed { i, m ->
                        if (i > 0) append(",")
                        append("{\"id\":${m.id},\"title\":\"${m.name}\"}")
                    }
                    append("]}")
                }
                context.contentResolver.openOutputStream(uri)?.use {
                    it.write(json.toByteArray())
                }
                _state.value = SettingsState.Success("Data exported successfully")
            } catch (e: Exception) {
                _state.value = SettingsState.Error(e.message ?: "Export failed")
            }
        }
    }

    fun importData(context: Context, uri: Uri) {
        viewModelScope.launch {
            _state.value = SettingsState.Loading
            try {
                context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                }
                _state.value = SettingsState.Success("Data imported successfully")
            } catch (e: Exception) {
                _state.value = SettingsState.Error(e.message ?: "Import failed")
            }
        }
    }

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