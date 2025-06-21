package com.duel.crydecoder.ui.classifier

data class ClassifierUiState(
    val isRecording: Boolean = false,
    val isLoading: Boolean = false,
    val resultText: String = "Press 'Start Recording' to begin"
)
