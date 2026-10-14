package com.example.reminders.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import com.example.reminders.ui.theme.*
import com.example.reminders.util.CategoryTone
import com.example.reminders.util.DateTimeUtil

@Composable
fun HeroCard(
    reminder: Reminder,
    tone: CategoryTone,
    onClick: () -> Unit,
    onDone: () -> Unit,
    onPauseResume: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPaused = reminder.paused
    val bgColor = if (isPaused) Neutral300 else tone.base
    val fgColor = if (isPaused) Neutral800 else tone.fg

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(HeroShape)
            .background(bgColor)
            .clickable(onClick = onClick)
    ) {
        // Decorative circles (far/RTL side = left in RTL)
        Box(
            modifier = Modifier
                .size(170.dp)
                .offset(x = (-40).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(
                    if (isPaused) Neutral200.copy(alpha = 0.55f)
                    else tone.c1.copy(alpha = 0.55f)
                )
        )
        Box(
            modifier = Modifier
                .size(70.dp)
                .offset(x = 34.dp, y = 62.dp)
                .clip(CircleShape)
                .background(
                    if (isPaused) Neutral400.copy(alpha = 0.6f)
                    else tone.c2.copy(alpha = 0.6f)
                )
        )

        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Kicker
            val kicker = if (isPaused) "בהשהיה · לא תופעל"
            else "הבא בתור · ${DateTimeUtil.relativeCountdown(DateTimeUtil.nextFireTimeMs(reminder))}"
            Text(text = kicker, style = AppTypography.labelMedium, color = fgColor.copy(alpha = 0.85f))

            // Name
            Text(text = reminder.name, style = AppTypography.headlineLarge, color = fgColor)

            // Meta
            val meta = buildMeta(reminder)
            Text(text = meta, style = AppTypography.bodyMedium, color = fgColor.copy(alpha = 0.85f))

            // Snooze indicator
            reminder.snoozeUntil?.let {
                Text(
                    text = "נודניק עד ${DateTimeUtil.formatTime(it)}",
                    style = AppTypography.labelLarge,
                    color = fgColor.copy(alpha = 0.85f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isPaused) {
                    // Done button
                    Button(
                        onClick = onDone,
                        colors = ButtonDefaults.buttonColors(containerColor = Bg, contentColor = tone.btnFg),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 9.dp),
                        shape = PillShape
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("סיימתי", style = AppTypography.labelLarge)
                    }
                    // Pause button
                    Button(
                        onClick = onPauseResume,
                        colors = ButtonDefaults.buttonColors(containerColor = tone.c2, contentColor = tone.pauseFg),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 9.dp),
                        shape = PillShape
                    ) {
                        Icon(Icons.Outlined.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("השהיה", style = AppTypography.labelLarge)
                    }
                } else {
                    // Resume button
                    Button(
                        onClick = onPauseResume,
                        colors = ButtonDefaults.buttonColors(containerColor = Accent2_600, contentColor = Bg),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 9.dp),
                        shape = PillShape
                    ) {
                        Icon(Icons.Outlined.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("המשך", style = AppTypography.labelLarge)
                    }
                }
            }
        }
    }
}

private fun buildMeta(reminder: Reminder): String {
    val timePart = when (reminder.type) {
        ReminderType.ONCE -> {
            val date = reminder.date ?: return ""
            val label = DateTimeUtil.hebrewDayLabel(reminder)
            "$label ב-${reminder.time}"
        }
        ReminderType.WEEKLY -> {
            val dayName = DateTimeUtil.hebrewDayOfWeekFull(reminder.day ?: 0)
            "כל יום $dayName ב-${reminder.time}"
        }
    }
    val typePart = if (reminder.type == ReminderType.ONCE) "חד-פעמית" else "שבועית"
    return "$timePart · $typePart"
}
