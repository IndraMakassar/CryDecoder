package com.duel.crydecoder

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.duel.crydecoder.ui.classifier.ClassifierScreen
import com.duel.crydecoder.ui.classifier.ClassifierViewModel
import com.duel.crydecoder.ui.history.HistoryScreen
import com.duel.crydecoder.ui.history.HistoryViewModel
import com.duel.crydecoder.ui.widget.NavBar
import com.duel.crydecoder.ui.widget.TopBar
import com.duel.crydecoder.ui.widget.navItems


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "home"

    val classifierViewModel: ClassifierViewModel = viewModel()
    val classifierUiState by classifierViewModel.uiState.collectAsState()

    val historyViewModel: HistoryViewModel = viewModel()
    val historyUiState by historyViewModel.uiState.collectAsState()

    val title = when (currentRoute) {
        "home" -> "Cry Decoder"
        "history" -> "History"
        else -> ""
    }

    androidx.compose.material3.Scaffold(
        topBar = { TopBar(title = title) },
        bottomBar = {
            NavBar(
                items = navItems,
                selectedRoute = currentRoute,
                onNavigate = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                ClassifierScreen(
                    uiState = classifierUiState,
                    onRecordClick = { classifierViewModel.onRecordClick() },
                    selectedRoute = currentRoute,
                    onNavigate = { navController.navigate(it) }
                )
            }
            composable("history") {
                HistoryScreen(
                    uiState = historyUiState,
                    selectedRoute = currentRoute,
                    onNavigate = { navController.navigate(it) }
                )
            }
        }
    }
}
