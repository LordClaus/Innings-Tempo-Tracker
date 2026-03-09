package com.client.inningstempotracker.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.client.inningstempotracker.data.db.MatchEntity
import com.client.inningstempotracker.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class InningsLibraryState {
    object Loading : InningsLibraryState()
    data class Success(val matches: List<MatchEntity>) : InningsLibraryState()
    data class Error(val message: String) : InningsLibraryState()
}

enum class SortOrder { DATE_DESC, DATE_ASC, FORMAT }

class InningsLibraryViewModel(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _state = MutableStateFlow<InningsLibraryState>(InningsLibraryState.Loading)
    val state: StateFlow<InningsLibraryState> = _state

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    private var allMatches: List<MatchEntity> = emptyList()

    init {
        loadMatches()
    }

    private fun loadMatches() {
        viewModelScope.launch {
            matchRepository.getAllMatches()
                .catch { e -> _state.value = InningsLibraryState.Error(e.message ?: "Error") }
                .collect { matches ->
                    allMatches = matches
                    applyFilters()
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun onSortOrderChange(order: SortOrder) {
        _sortOrder.value = order
        applyFilters()
    }

    private fun applyFilters() {
        val query = _searchQuery.value.lowercase()
        val filtered = if (query.isEmpty()) {
            allMatches
        } else {
            allMatches.filter { it.name.lowercase().contains(query) }
        }

        val sorted = when (_sortOrder.value) {
            SortOrder.DATE_DESC -> filtered.sortedByDescending { it.createdAt }
            SortOrder.DATE_ASC -> filtered.sortedBy { it.createdAt }
            SortOrder.FORMAT -> filtered.sortedBy { it.format }
        }

        _state.value = InningsLibraryState.Success(sorted)
    }
}