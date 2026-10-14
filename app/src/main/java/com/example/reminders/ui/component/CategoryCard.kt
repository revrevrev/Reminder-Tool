package com.example.reminders.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.reminders.data.model.Category
import com.example.reminders.ui.theme.*
import com.example.reminders.util.paletteColor

@Composable
fun CategoryCard(
    category: Category,
    reminderCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dotColor = paletteColor(category.color)
    val countText = when (reminderCount) {
        0 -> "אין תזכורות"
        1 -> "תזכורת אחת"
        else -> "$reminderCount תזכורות"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Color circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(dotColor)
        )

        // Name + count
        Column(modifier = Modifier.weight(1f)) {
            Text(text = category.name, style = AppTypography.titleMedium, color = TextColor)
            Text(text = countText, style = AppTypography.labelMedium, color = Neutral600)
        }

        // Sound + volume
        Column(horizontalAlignment = Alignment.End) {
            Text(text = category.sound, style = AppTypography.titleSmall, color = TextColor)
            Text(text = "עוצמה ${category.volume}%", style = AppTypography.labelMedium, color = Neutral600)
        }

        Icon(Icons.Outlined.ChevronLeft, contentDescription = null, tint = Neutral700)
    }
}

@Composable
fun NewCategoryCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .border(2.dp, Neutral400, MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(Icons.Outlined.Add, contentDescription = null, tint = Neutral600)
        Text("+ קטגוריה חדשה", style = AppTypography.titleMedium, color = Neutral600)
    }
}
