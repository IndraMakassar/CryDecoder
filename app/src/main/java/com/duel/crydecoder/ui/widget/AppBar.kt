package com.duel.crydecoder.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.duel.crydecoder.R
import com.duel.crydecoder.ui.classifier.ClassifierUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()), uiState: ClassifierUiState){
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { /* do something */ }) {
                if (uiState.isPremium) {
                    Box(
                        modifier = Modifier.size(32.dp)
                            .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.crown),
                            contentDescription = "Crown Icon",
                            modifier = Modifier
                                .size(26.dp)
                                .padding(4.dp)
                            ,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}