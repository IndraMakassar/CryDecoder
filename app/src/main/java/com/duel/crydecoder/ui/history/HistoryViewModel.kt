package com.duel.crydecoder.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
class HistoryViewModel (application: Application) : AndroidViewModel(application)  {
    private val _uiState = MutableStateFlow(HistoryUiState())

    val uiState: StateFlow<HistoryUiState> = _uiState
    fun addHistoryItem(item: HistoryUiState) {
        _uiState.update {
            it.copy(items = listOf(item) + it.items) // tambah di atas
        }
    }
}