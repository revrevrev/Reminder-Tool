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
import com.example.reminders.util.DateTimeUtil

@Composable
fun WeekdayPicker(
    selectedDay: Int?,   // 0=Sun..6=Sat
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = DateTimeUtil.weekdayPickerOrder()
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
    ) {
        // In RTL layout, first item appears on the right. Sunday (א, index 0) is first.
        days.forEach { (label, dayIndex) ->
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
