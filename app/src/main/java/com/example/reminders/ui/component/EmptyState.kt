package com.example.reminders.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import com.example.reminders.ui.theme.*

@Composable
fun EmptyState(onAddClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Illustration: large circle with overlapping smaller circle and bell
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Accent2_200)
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomStart)
                    .clip(CircleShape)
                    .background(Accent300)
            )
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Accent2_800
            )
        }
        Text(
            text = "אין תזכורות כרגע",
            style = AppTypography.headlineMedium,
            color = TextColor,
            textAlign = TextAlign.Center
        )
        Text(
            text = "הוסיפו תזכורת חד-פעמית או שבועית, ונזכיר לכם בזמן.",
            style = AppTypography.bodyMedium,
            color = Neutral700,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 270.dp)
        )
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = AccentMain, contentColor = Bg)
        ) {
            Text("+ תזכורת חדשה", style = AppTypography.titleSmall)
        }
    }
}

@Composable
fun FilterEmptyState(categoryName: String, onShowAll: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.large)
            .background(Surface)
            .padding(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "אין תזכורות פעילות בקטגוריה $categoryName",
                style = AppTypography.bodyMedium,
                color = TextColor,
                textAlign = TextAlign.Center
            )
            OutlinedButton(onClick = onShowAll) {
                Text("הצגת הכל", style = AppTypography.labelLarge)
            }
        }
    }
}
