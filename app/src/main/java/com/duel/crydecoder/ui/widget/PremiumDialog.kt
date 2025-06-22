package com.duel.crydecoder.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PremiumDialog(
    title: String,
    features: List<String>,
    button: String,
    onConfirm: () -> Unit
) {
    Card (
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .shadow(elevation = 4.dp, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column (
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ){
            Text(
                text = title,
                modifier = androidx.compose.ui.Modifier.align(Alignment.CenterHorizontally)
            )
            HorizontalDivider(
                modifier = androidx.compose.ui.Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
            features.forEach { feature ->
                Text(
                    text = feature,
                    modifier = androidx.compose.ui.Modifier.padding(vertical = 4.dp)
                )
            }
            Divider(thickness = 1.dp, color = Color.Gray)
            Button(
                onClick = onConfirm,
                modifier =
                    androidx.compose.ui.Modifier.size(200.dp)
                        .shadow(elevation = 16.dp, shape = androidx.compose.foundation.shape.CircleShape),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ){
                Text(text = button)
                onConfirm()
            }
        }
    }
}