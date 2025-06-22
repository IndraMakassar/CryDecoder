package com.duel.crydecoder.ui.widget

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PremiumFeatureAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Premium Features")
        },
        text = {
            Column {
                PremiumDialogContent(
                    title = "Basic (Now)",
                    features = listOf("Detailed explanation", "Cry timeline", "Smart suggestions")
                )
                PremiumDialogContent(
                    title = "Premium 👑",
                    features = listOf("More cry classifications", "Includes serious conditions")
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Subscribe Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Maybe Later")
            }
        }
    )
}
