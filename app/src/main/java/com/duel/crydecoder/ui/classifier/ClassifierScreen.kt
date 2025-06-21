package com.duel.crydecoder.ui.classifier

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.  Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.duel.crydecoder.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassifierScreen(uiState: ClassifierUiState, onRecordClick: () -> Unit, selectedRoute: String, onNavigate: (String) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberScrollState()
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
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Card (
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .shadow(elevation = 4.dp, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .width(150.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ){
            Row (
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.crown),
                    contentDescription = "Crown Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Try Our Premium",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
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
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRecordClick,
            modifier =
                Modifier.size(200.dp)
                .shadow(elevation = 16.dp, shape = androidx.compose.foundation.shape.CircleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
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
        Spacer(modifier = Modifier.height(16.dp))
        // Result Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            }
            else if (!uiState.isRecording && !uiState.isResultReady){
                Text(
                    text = uiState.resultText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            else if(uiState.isRecording && !uiState.isResultReady){
                Text(
                    text = uiState.resultText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            else if (uiState.isResultReady) {
                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    ,
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.resultText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (keywords.any { keyword -> uiState.resultText.contains(keyword, ignoreCase = true) }) {
                            Text(
                                text = explanationText,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
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

