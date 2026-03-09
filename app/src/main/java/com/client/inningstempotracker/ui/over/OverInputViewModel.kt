package com.client.inningstempotracker.ui.over

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.client.inningstempotracker.data.db.OverEntity
import com.client.inningstempotracker.data.repository.OverRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class OverInputState {
    object Loading : OverInputState()
    data class Success(val overs: List<OverEntity>) : OverInputState()
    data class Error(val message: String) : OverInputState()
}

class OverInputViewModel(
    private val overRepository: OverRepository
) : ViewModel() {

    private val _state = MutableStateFlow<OverInputState>(OverInputState.Loading)
    val state: StateFlow<OverInputState> = _state

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

    fun init(matchId: Int) {
        _matchId.value = matchId
        loadOvers()
    }

    private fun loadOvers() {
        viewModelScope.launch {
            overRepository.getOversByMatchId(_matchId.value)
                .catch { e -> _state.value = OverInputState.Error(e.message ?: "Error") }
                .collect { overs -> _state.value = OverInputState.Success(overs) }
        }
    }

    fun onRunsChange(value: String) {
        _runs.value = value
        _runsError.value = null
    }

    fun onWicketChange(value: Boolean) { _wicket.value = value }
    fun onPhaseTypeChange(value: String) { _phaseType.value = value }
    fun onNoteChange(value: String) { _note.value = value }

    fun showAddForm() {
        _editingOver.value = null
        _runs.value = "0"
        _wicket.value = false
        _phaseType.value = "Powerplay"
        _note.value = ""
        _runsError.value = null
        _showForm.value = true
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

    fun saveOver() {
        if (!validate()) return
        viewModelScope.launch {
            val currentOvers = (state.value as? OverInputState.Success)?.overs ?: emptyList()
            val editing = _editingOver.value
            if (editing != null) {
                overRepository.updateOver(
                    editing.copy(
                        runs = _runs.value.toInt(),
                        wicket = _wicket.value,
                        phaseType = _phaseType.value,
                        note = _note.value.ifEmpty { null }
                    )
                )
            } else {
                overRepository.insertOver(
                    OverEntity(
                        matchId = _matchId.value,
                        overNumber = currentOvers.size + 1,
                        runs = _runs.value.toInt(),
                        wicket = _wicket.value,
                        phaseType = _phaseType.value,
                        note = _note.value.ifEmpty { null }
                    )
                )
            }
            _showForm.value = false
        }
    }

    fun deleteOver(over: OverEntity) {
        viewModelScope.launch {
            overRepository.deleteOver(over)
        }
    }

    private fun validate(): Boolean {
        val runsInt = _runs.value.toIntOrNull()
        if (runsInt == null || runsInt < 0 || runsInt > 200) {
            _runsError.value = "Invalid run count (0-200)"
            return false
        }
        return true
    }
}