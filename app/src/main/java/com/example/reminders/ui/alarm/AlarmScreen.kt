package com.example.reminders.ui.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.reminders.data.model.ReminderType
import com.example.reminders.ui.component.PulsingRings
import com.example.reminders.ui.component.SnoozePanel
import com.example.reminders.ui.theme.*
import com.example.reminders.util.DateTimeUtil
import com.example.reminders.util.categoryTone
import com.example.reminders.viewmodel.AlarmViewModel

@Composable
fun AlarmScreen(
    vm: AlarmViewModel,
    onSnooze: () -> Unit,
    onDone: () -> Unit
) {
    val state by vm.uiState.collectAsState()
    val reminder = state.reminder ?: return
    val catColor = state.category?.color ?: "accent-500"
    val tone = categoryTone(catColor)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tone.base)
    ) {
        // Decorative circle top-right (in RTL = top-start, renders top-left physically)
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = 80.dp, y = (-80).dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(tone.c2.copy(alpha = 0.45f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date + time at top
            Spacer(Modifier.height(44.dp))
            Text(
                text = "היום",
                style = AppTypography.labelLarge,
                color = tone.fg.copy(alpha = 0.85f)
            )
            Text(
                text = DateTimeUtil.formatTime(System.currentTimeMillis()),
                style = AppTypography.displayLarge,
                color = tone.fg
            )

            Spacer(Modifier.height(20.dp))

            // Pulsing rings + bell
            PulsingRings(
                ringColor = tone.c1,
                discColor = Bg,
                iconColor = tone.icon
            )

            Spacer(Modifier.height(20.dp))

            // Name + notes
            Text(
                text = reminder.name,
                style = AppTypography.displayMedium,
                color = tone.fg
            )
            reminder.notes?.let {
                Spacer(Modifier.height(8.dp))
                Text(text = it, style = AppTypography.bodyLarge, color = tone.fg.copy(alpha = 0.85f))
            }

            Spacer(Modifier.weight(1f))

            // Bottom panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Bg)
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SnoozePanel(
                    snoozeMinutes = state.snoozeMinutes,
                    onSnoozeChange = { vm.setSnoozeMinutes(it) },
                    onSnoozeNow = { minutes ->
                        vm.setSnoozeMinutes(minutes)
                        vm.snooze(onSnooze)
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Snooze button
                    OutlinedButton(
                        onClick = { vm.snooze(onSnooze) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        shape = PillShape
                    ) {
                        Text("נודניק", style = AppTypography.titleMedium)
                    }

                    // Done button
                    val doneLabel = if (reminder.type == ReminderType.ONCE) "בוצע" else "בוצע"
                    Button(
                        onClick = { vm.markDone(onDone) },
                        modifier = Modifier.weight(1.6f),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentMain, contentColor = Bg)
                    ) {
                        Text(doneLabel, style = AppTypography.titleMedium)
                    }
                }
            }
        }
    }
}

