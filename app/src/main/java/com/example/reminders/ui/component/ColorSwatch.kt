package com.example.reminders.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.reminders.data.model.CATEGORY_COLORS
import com.example.reminders.ui.theme.Bg
import com.example.reminders.ui.theme.TextColor
import com.example.reminders.util.paletteColor

@Composable
fun ColorSwatchRow(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CATEGORY_COLORS.forEach { token ->
            val color = paletteColor(token)
            val selected = token == selectedColor
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (selected) Modifier
                            .border(3.dp, Bg, CircleShape)
                            .border(5.dp, TextColor, CircleShape)
                        else Modifier
                    )
                    .clickable { onColorSelected(token) }
            )
        }
    }
}
