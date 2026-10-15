package com.example.reminders.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
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
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Color dot — 14dp
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(dotColor)
        )

        // Name + count
        Column(modifier = Modifier.weight(1f)) {
            Text(text = category.name, style = AppTypography.titleMedium, color = TextColor)
            Text(text = countText, style = AppTypography.labelMedium, color = Neutral700)
        }

        // Trailing: sound name only
        Text(
            text = category.sound,
            style = AppTypography.bodySmall,
            color = Neutral700
        )
    }
}

@Composable
fun NewCategoryButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        onClick = onClick,
        modifier = modifier.padding(top = 6.dp),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
    ) {
        Icon(Icons.Outlined.Add, contentDescription = null,
            modifier = Modifier.size(16.dp), tint = Accent700)
        Spacer(Modifier.width(6.dp))
        Text("קטגוריה חדשה", style = AppTypography.labelLarge, color = Accent700)
    }
}
