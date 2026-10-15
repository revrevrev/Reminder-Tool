package com.example.reminders.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reminders.data.model.Category
import com.example.reminders.ui.theme.*
import com.example.reminders.util.paletteColor
import com.example.reminders.viewmodel.SettingsViewModel

private val SNOOZE_OPTIONS = listOf(5, 10, 15, 30)

@Composable
fun SettingsScreen(
    onNavigateToArchive: () -> Unit,
    vm: SettingsViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text("הגדרות", style = AppTypography.headlineLarge, color = TextColor,
            modifier = Modifier.padding(horizontal = 8.dp))
        Spacer(Modifier.height(4.dp))

        // Default snooze card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(Surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("משך נודניק", style = AppTypography.titleMedium, color = TextColor)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SNOOZE_OPTIONS.forEach { min ->
                    val selected = state.defaultSnooze == min
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(PillShape)
                            .background(if (selected) AccentMain else Neutral200)
                            .clickable { vm.setDefaultSnooze(min) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$min דק׳",
                            style = AppTypography.labelLarge,
                            color = if (selected) Bg else TextColor
                        )
                    }
                }
            }
        }

        // Default category card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(Surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("קטגוריית ברירת מחדל", style = AppTypography.titleMedium, color = TextColor)
                val subtitle = if (state.defaultCategoryId != null)
                    "נבחרת מראש בכל תזכורת חדשה"
                else
                    "בכל תזכורת חדשה תתבקשו לבחור קטגוריה"
                Text(subtitle, style = AppTypography.bodySmall, color = Neutral700)
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DefaultCategoryChip(
                    label = "ללא",
                    dotColor = null,
                    selected = state.defaultCategoryId == null,
                    onClick = { vm.setDefaultCategory(null) }
                )
                state.categories.forEach { cat ->
                    DefaultCategoryChip(
                        label = cat.name,
                        dotColor = cat,
                        selected = cat.id == state.defaultCategoryId,
                        onClick = { vm.setDefaultCategory(cat.id) }
                    )
                }
            }
        }

        // Archive row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(Surface)
                .clickable(onClick = onNavigateToArchive)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ארכיון", style = AppTypography.titleMedium, color = TextColor)
                val subtitle = if (state.archivedCount == 0) "ריק"
                else "${state.archivedCount} תזכורות חד-פעמיות שהושלמו"
                Text(subtitle, style = AppTypography.bodySmall, color = Neutral700)
            }
            Icon(Icons.Outlined.ChevronLeft, contentDescription = null, tint = Neutral600)
        }
    }
}

@Composable
private fun DefaultCategoryChip(
    label: String,
    dotColor: Category?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderMod = if (selected) Modifier.border(2.dp, TextColor, PillShape) else Modifier

    Row(
        modifier = modifier
            .clip(PillShape)
            .background(Bg)
            .then(borderMod)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (dotColor != null) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(paletteColor(dotColor.color))
            )
        }
        Text(text = label, style = AppTypography.titleSmall, color = TextColor)
    }
}
