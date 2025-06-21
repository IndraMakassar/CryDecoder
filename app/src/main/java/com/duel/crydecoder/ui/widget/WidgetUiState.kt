package com.duel.crydecoder.ui.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    NavItem("Home", Icons.Default.Home, "home"),
    NavItem("History", Icons.Default.History, "history")
)
