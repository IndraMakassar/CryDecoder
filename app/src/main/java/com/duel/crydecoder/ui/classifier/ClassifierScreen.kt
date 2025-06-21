package com.duel.crydecoder.ui.classifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import com.duel.crydecoder.ui.widget.NavBar
import com.duel.crydecoder.ui.widget.TopBar
import com.duel.crydecoder.ui.widget.navItems


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassifierScreen(uiState: ClassifierUiState, onRecordClick: () -> Unit, selectedRoute: String, onNavigate: (String) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val keywords = listOf("belly_pain", "burping", "discomfort", "hungry", "tired")
    val keywordDescriptions = mapOf(
        "belly_pain" to "The baby may be experiencing stomach pain.",
        "burping" to "The baby might need to burp.",
        "discomfort" to "The baby feels uncomfortable.",
        "hungry" to "The baby is hungry and needs to be fed.",
        "tired" to "The baby is tired and likely needs sleep."
    )
    val matchedKeyword = keywordDescriptions.keys.find { keyword ->
        uiState.resultText.contains(keyword, ignoreCase = true)
    }
    val explanationText = matchedKeyword?.let { keywordDescriptions[it] } ?: "No specific cry detected."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Baby Cry Classifier",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Detect if your baby is hungry, tired, in pain, or needs a diaper change.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = onRecordClick,
            modifier = Modifier.size(200.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ),
            enabled = !uiState.isLoading,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (uiState.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (uiState.isRecording) "Recording" else "Paused",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(90.dp)
            )
        }
        // Result Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            } else {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.resultText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    if (keywords.any { keyword -> uiState.resultText.contains(keyword, ignoreCase = true) }) {
                        Text(
                            text = explanationText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun ClassifierScreenPreview() {
    val fakeUiState = ClassifierUiState()
    var selected by remember { mutableStateOf("home") }

    ClassifierScreen(
        uiState = fakeUiState,
        onRecordClick = {},
        selectedRoute = selected,
        onNavigate = { selected = it }
    )
}

