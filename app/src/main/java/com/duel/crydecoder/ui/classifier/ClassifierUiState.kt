package com.duel.crydecoder.ui.classifier

data class ClassifierUiState(
    val isRecording: Boolean = false,
    val isLoading: Boolean = false,
    val isResultReady: Boolean = false,
    val resultText: String = "Press Button to Begin",
    val isPremium: Boolean = false,
)
