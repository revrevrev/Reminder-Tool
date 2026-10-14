package com.example.reminders.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reminders.data.model.Reminder
import com.example.reminders.ui.component.ToastHost
import com.example.reminders.ui.theme.*
import com.example.reminders.util.DateTimeUtil
import com.example.reminders.viewmodel.ArchiveViewModel

@Composable
fun ArchiveScreen(
    onBack: () -> Unit,
    onOpenEdit: (Int) -> Unit,
    vm: ArchiveViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 110.dp)) {
            item {
                Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 14.dp)) {
                    TextButton(
                        onClick = onBack,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = AccentMain, modifier = Modifier.size(18.dp))
                        Text("הגדרות", style = AppTypography.labelLarge, color = AccentMain)
                    }
                    Text("ארכיון", style = AppTypography.headlineLarge, color = TextColor)
                    Text("תזכורות חד-פעמיות שהושלמו", style = AppTypography.bodyMedium, color = Neutral700)
                }
            }

            if (state.reminders.isEmpty()) {
                item {
                    Text(
                        "הארכיון ריק. כשמסמנים תזכורת חד-פעמית כ\u201Eסיימתי\u201D, היא עוברת לכאן.",
                        style = AppTypography.bodyMedium,
                        color = Neutral700,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            } else {
                items(state.reminders, key = { it.id }) { reminder ->
                    ArchiveRow(
                        reminder = reminder,
                        onRestore = { vm.restore(reminder) { onOpenEdit(it) } },
                        onDelete = { vm.delete(reminder) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Toast
        state.toast?.let { toast ->
            ToastHost(
                toast = toast,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun ArchiveRow(
    reminder: Reminder,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category dot - we'd need category color here, using a placeholder
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Accent500)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reminder.name,
                style = AppTypography.titleSmall.copy(textDecoration = TextDecoration.LineThrough),
                color = Neutral500
            )
            val archivedMs = reminder.archivedAt ?: System.currentTimeMillis()
            Text(
                text = "${reminder.date ?: ""} ${reminder.time} · הושלם ${DateTimeUtil.hebrewDateFull(archivedMs)}",
                style = AppTypography.labelMedium,
                color = Neutral600
            )
        }

        OutlinedButton(
            onClick = onRestore,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("שחזר", style = AppTypography.labelLarge)
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Outlined.Delete, contentDescription = "מחק", tint = Accent700, modifier = Modifier.size(18.dp))
        }
    }
}
