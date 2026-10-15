package com.example.reminders.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.reminders.data.model.Reminder
import com.example.reminders.ui.theme.*
import com.example.reminders.util.DateTimeUtil
import com.example.reminders.util.categoryTone

@Composable
fun ReminderRow(
    reminder: Reminder,
    categoryColor: String,
    onClick: () -> Unit,
    onPauseResume: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPaused = reminder.paused
    val rowBg = if (isPaused) Neutral200 else Color.Transparent
    val nameColor = if (isPaused) Neutral500 else TextColor
    val timeColor = if (isPaused) Neutral500 else TextColor
    val barColor = if (isPaused) Neutral300 else categoryTone(categoryColor).base
    val iconTint = if (isPaused) Neutral500 else Neutral700

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(rowBg)
            .clickable(onClick = onClick)
            .padding(11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Time column
        Column(
            modifier = Modifier.width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = DateTimeUtil.hebrewDayLabel(reminder),
                style = AppTypography.labelSmall,
                color = if (isPaused) Neutral500 else Neutral700
            )
            Text(
                text = reminder.time,
                style = AppTypography.displaySmall,
                color = timeColor
            )
        }

        // Color bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(barColor)
        )

        // Name + sub
        Column(modifier = Modifier.weight(1f)) {
            Text(text = reminder.name, style = AppTypography.titleSmall, color = nameColor)
            when {
                reminder.snoozeUntil != null ->
                    Text("נודניק עד ${DateTimeUtil.formatTime(reminder.snoozeUntil)}", style = AppTypography.labelMedium, color = if (isPaused) Neutral500 else Accent700)
                isPaused ->
                    Text("בהשהיה", style = AppTypography.labelMedium, color = Neutral700)
            }
        }

        // Pause/Resume icon button
        IconButton(onClick = onPauseResume, modifier = Modifier.size(34.dp)) {
            Icon(
                imageVector = if (isPaused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                contentDescription = if (isPaused) "המשך" else "השהיה",
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
