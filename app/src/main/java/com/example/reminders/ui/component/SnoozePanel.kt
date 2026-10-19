package com.example.reminders.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.reminders.ui.theme.*

private val SNOOZE_PRESETS = listOf(5, 10, 15, 30)

internal fun applySnoozePreset(
    preset: Int,
    onSnoozeChange: (Int) -> Unit,
    onSnoozeNow: ((Int) -> Unit)?
) {
    onSnoozeChange(preset)
    onSnoozeNow?.invoke(preset)
}

@Composable
fun SnoozePanel(
    snoozeMinutes: Int,
    onSnoozeChange: (Int) -> Unit,
    onSnoozeNow: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Bg)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Label + stepper
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("נודניק", style = AppTypography.titleSmall, color = TextColor, modifier = Modifier.weight(1f))

            // − button
            IconButton(
                onClick = { if (snoozeMinutes > 1) onSnoozeChange(snoozeMinutes - 1) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(PillShape)
                    .background(Surface)
            ) {
                Icon(Icons.Outlined.Remove, contentDescription = "פחות", tint = TextColor)
            }

            Text(
                text = "$snoozeMinutes דק׳",
                style = AppTypography.titleLarge,
                color = TextColor,
                modifier = Modifier.widthIn(min = 64.dp),
            )

            // + button
            IconButton(
                onClick = { if (snoozeMinutes < 120) onSnoozeChange(snoozeMinutes + 1) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(PillShape)
                    .background(Surface)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "יותר", tint = TextColor)
            }
        }

        // Presets row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SNOOZE_PRESETS.forEach { preset ->
                val selected = snoozeMinutes == preset
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(if (selected) AccentMain else Surface)
                        .clickable { applySnoozePreset(preset, onSnoozeChange, onSnoozeNow) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$preset",
                        style = AppTypography.labelLarge,
                        color = if (selected) Bg else TextColor
                    )
                }
            }
        }
    }
}

