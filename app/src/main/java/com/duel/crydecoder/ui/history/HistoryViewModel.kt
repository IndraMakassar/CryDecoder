package com.duel.crydecoder.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistoryViewModel (application: Application) : AndroidViewModel(application)  {
    // Internal mutable state
    private val _uiState = MutableStateFlow(HistoryUiState())

    // External immutable state
    val uiState: StateFlow<HistoryUiState> = _uiState
}