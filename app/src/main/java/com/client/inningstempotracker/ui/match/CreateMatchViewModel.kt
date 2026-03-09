package com.client.inningstempotracker.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.client.inningstempotracker.data.db.MatchEntity
import com.client.inningstempotracker.data.repository.MatchRepository
import com.client.inningstempotracker.utils.DateFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CreateMatchState {
    object Idle : CreateMatchState()
    object Loading : CreateMatchState()
    data class Success(val matchId: Int) : CreateMatchState()
    data class Error(val message: String) : CreateMatchState()
}

class CreateMatchViewModel(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CreateMatchState>(CreateMatchState.Idle)
    val state: StateFlow<CreateMatchState> = _state

    val formats = listOf("T20", "ODI", "Test", "Custom")

    private val _matchName = MutableStateFlow("")
    val matchName: StateFlow<String> = _matchName

    private val _matchFormat = MutableStateFlow("T20")
    val matchFormat: StateFlow<String> = _matchFormat

    private val _matchDate = MutableStateFlow(DateFormatter.getCurrentDateForStorage())
    val matchDate: StateFlow<String> = _matchDate

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError

    fun onNameChange(value: String) {
        _matchName.value = value
        _nameError.value = null
    }

    fun onFormatChange(value: String) {
        _matchFormat.value = value
    }

    fun onDateChange(value: String) {
        _matchDate.value = value
        _dateError.value = null
    }

    fun createMatch() {
        if (!validate()) return

        viewModelScope.launch {
            _state.value = CreateMatchState.Loading
            try {
                val match = MatchEntity(
                    name = _matchName.value.trim(),
                    format = _matchFormat.value,
                    date = _matchDate.value
                )
                val id = matchRepository.insertMatch(match)
                _state.value = CreateMatchState.Success(id.toInt())
            } catch (e: Exception) {
                _state.value = CreateMatchState.Error(e.message ?: "Failed to create match")
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        if (_matchName.value.trim().isEmpty()) {
            _nameError.value = "Match name is required"
            isValid = false
        } else if (_matchName.value.length > 100) {
            _nameError.value = "Max 100 characters"
            isValid = false
        }

        if (!DateFormatter.isValidDate(_matchDate.value)) {
            _dateError.value = "Invalid date"
            isValid = false
        } else if (!DateFormatter.isNotFutureDate(_matchDate.value)) {
            _dateError.value = "Date cannot be in the future"
            isValid = false
        }

        return isValid
    }

    fun resetState() {
        _state.value = CreateMatchState.Idle
    }
}