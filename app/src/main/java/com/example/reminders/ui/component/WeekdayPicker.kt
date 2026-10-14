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
import com.example.reminders.ui.theme.*

private val DAYS_RTL = listOf("ש׳", "ו׳", "ה׳", "ד׳", "ג׳", "ב׳", "א׳") // Sat..Sun displayed RTL
private val DAY_INDICES = listOf(6, 5, 4, 3, 2, 1, 0) // 6=Sat..0=Sun

@Composable
fun WeekdayPicker(
    selectedDay: Int?,   // 0=Sun..6=Sat
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
    ) {
        // In RTL layout, first item appears on the right, so we display Sat first (index 6) which renders rightmost
        DAYS_RTL.forEachIndexed { i, label ->
            val dayIndex = DAY_INDICES[i]
            val selected = selectedDay == dayIndex
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (selected) Accent2_600 else Surface)
                    .border(if (!selected) 1.dp else 0.dp, Neutral300, CircleShape)
                    .clickable { onDaySelected(dayIndex) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = AppTypography.labelLarge,
                    color = if (selected) Bg else TextColor
                )
            }
        }
    }
}
