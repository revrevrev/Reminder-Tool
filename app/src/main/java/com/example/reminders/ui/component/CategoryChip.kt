package com.example.reminders.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.reminders.data.model.Category
import com.example.reminders.ui.theme.*
import com.example.reminders.util.paletteColor

@Composable
fun CategoryFilterChip(
    label: String,
    dotColor: androidx.compose.ui.graphics.Color? = null,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) TextColor else Surface
    val fg = if (selected) Bg else TextColor

    Row(
        modifier = modifier
            .clip(PillShape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        dotColor?.let {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(it)
            )
        }
        Text(text = label, style = AppTypography.labelLarge, color = fg)
    }
}

@Composable
fun CategorySelectorChip(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dotColor = paletteColor(category.color)
    val borderMod = if (selected)
        Modifier.border(2.dp, TextColor, PillShape)
    else Modifier

    Row(
        modifier = modifier
            .clip(PillShape)
            .background(if (selected) Bg else Surface)
            .then(borderMod)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(text = category.name, style = AppTypography.titleSmall, color = TextColor)
    }
}
