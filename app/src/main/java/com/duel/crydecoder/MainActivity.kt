package com.duel.crydecoder

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duel.crydecoder.ui.classifier.ClassifierScreen
import com.duel.crydecoder.ui.classifier.ClassifierViewModel
import com.duel.crydecoder.ui.theme.CryDecoderTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted. The ViewModel will handle checks.
            } else {
                // You can optionally show a message to the user that permission is needed.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for audio permission when the app starts
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        setContent {
            CryDecoderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This properly initializes the ViewModel
                    val viewModel: ClassifierViewModel = viewModel()
                    // This collects the state from the ViewModel
                    val uiState by viewModel.uiState.collectAsState()

                    // This calls your UI, passing the state and the click handler
                    ClassifierScreen(
                        uiState = uiState,
                        onRecordClick = { viewModel.onRecordClick() }
                    )
                }
            }
        }
    }
}
