package com.duel.crydecoder.ui.history

data class HistoryUiState(
    val items: List<HistoryUiState> = emptyList(),
    val isLoading: Boolean = false,
    val title: String = "Title",
    val explanation: String = "specify explanation",
    val timestamp: Long = System.currentTimeMillis(),
)
