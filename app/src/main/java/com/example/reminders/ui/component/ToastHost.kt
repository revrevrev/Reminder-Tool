package com.example.reminders.ui.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import com.example.reminders.ui.theme.*

data class ToastData(
    val message: String,
    val undoLabel: String? = null,
    val onUndo: (() -> Unit)? = null
)

@Composable
fun ToastHost(
    toast: ToastData?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = toast != null,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
        modifier = modifier
    ) {
        toast?.let { t ->
            Row(
                modifier = Modifier
                    .clip(PillShape)
                    .background(Neutral900)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = t.message,
                    style = AppTypography.bodyMedium,
                    color = Bg
                )
                if (t.undoLabel != null && t.onUndo != null) {
                    TextButton(onClick = t.onUndo) {
                        Text(
                            text = t.undoLabel,
                            style = AppTypography.labelLarge,
                            color = Accent300
                        )
                    }
                }
            }
        }
    }
}
