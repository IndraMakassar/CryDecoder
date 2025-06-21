package com.duel.crydecoder.ui.classifier

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.History
data class ClassifierUiState(
    val isRecording: Boolean = false,
    val isLoading: Boolean = false,
    val resultText: String = "Press 'Start Recording' to begin"
)

enum class Screen(val label: String, val icon: ImageVector, val route: String) {
    HOME("Home", Icons.Default.Home, "home"),
    HISTORY("History", Icons.Default.History, "com/duel/crydecoder/ui/history")
}
