package com.client.inningstempotracker.ui.edit

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

sealed class EditInningState {
    object Loading : EditInningState()
    data class Success(
        val match: MatchEntity,
        val overs: List<OverEntity>
    ) : EditInningState()
    data class Error(val message: String) : EditInningState()
}

class EditInningViewModel(
    private val matchRepository: MatchRepository,
    private val overRepository: OverRepository
) : ViewModel() {

    private val _state = MutableStateFlow<EditInningState>(EditInningState.Loading)
    val state: StateFlow<EditInningState> = _state

    private val _matchId = MutableStateFlow(0)

    private val _runs = MutableStateFlow("0")
    val runs: StateFlow<String> = _runs

    private val _wicket = MutableStateFlow(false)
    val wicket: StateFlow<Boolean> = _wicket

    private val _phaseType = MutableStateFlow("Powerplay")
    val phaseType: StateFlow<String> = _phaseType

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note

    private val _runsError = MutableStateFlow<String?>(null)
    val runsError: StateFlow<String?> = _runsError

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm

    private val _editingOver = MutableStateFlow<OverEntity?>(null)
    val editingOver: StateFlow<OverEntity?> = _editingOver

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun init(matchId: Int) {
        _matchId.value = matchId
        viewModelScope.launch {
            val match = matchRepository.getMatchById(matchId)
            if (match == null) {
                _state.value = EditInningState.Error("Match not found")
                return@launch
            }
            overRepository.getOversByMatchId(matchId)
                .catch { e -> _state.value = EditInningState.Error(e.message ?: "Error") }
                .collect { overs ->
                    _state.value = EditInningState.Success(match = match, overs = overs)
                }
        }
    }

    fun showEditForm(over: OverEntity) {
        _editingOver.value = over
        _runs.value = over.runs.toString()
        _wicket.value = over.wicket
        _phaseType.value = over.phaseType
        _note.value = over.note ?: ""
        _runsError.value = null
        _showForm.value = true
    }

    fun hideForm() { _showForm.value = false }

    fun onRunsChange(value: String) {
        _runs.value = value
        _runsError.value = null
    }

    fun onWicketChange(value: Boolean) { _wicket.value = value }
    fun onPhaseTypeChange(value: String) { _phaseType.value = value }
    fun onNoteChange(value: String) { _note.value = value }

    fun saveChanges() {
        val runsInt = _runs.value.toIntOrNull()
        if (runsInt == null || runsInt < 0 || runsInt > 200) {
            _runsError.value = "Invalid run count (0-200)"
            return
        }
        viewModelScope.launch {
            val editing = _editingOver.value ?: return@launch
            overRepository.updateOver(
                editing.copy(
                    runs = runsInt,
                    wicket = _wicket.value,
                    phaseType = _phaseType.value,
                    note = _note.value.ifEmpty { null }
                )
            )
            _showForm.value = false
        }
    }

    fun deleteOver(over: OverEntity) {
        viewModelScope.launch {
            overRepository.deleteOver(over)
        }
    }

    fun confirmSave() {
        _saveSuccess.value = true
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}