package com.duel.crydecoder.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel (application: Application) : AndroidViewModel(application)  {
    private val storage = HistoryStorage(application)
    private val _uiState = MutableStateFlow(HistoryUiState())

    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        loadHistory()
    }

    fun addHistoryItem(item: HistoryUiState) {
        val updated = listOf(item) + _uiState.value.items
        _uiState.update { it.copy(items = updated) }

        viewModelScope.launch {
            storage.save(updated)
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val saved = storage.load()
            _uiState.update { it.copy(items = saved) }
        }
    }
}