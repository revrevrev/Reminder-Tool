package com.example.reminders.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reminders.data.model.Reminder
import com.example.reminders.ui.component.*
import com.example.reminders.ui.sheet.ReminderSheet
import com.example.reminders.ui.theme.*
import com.example.reminders.util.DateTimeUtil
import com.example.reminders.util.categoryTone
import com.example.reminders.viewmodel.HomeViewModel
import com.example.reminders.viewmodel.ReminderSheetViewModel

@Composable
fun HomeScreen(
    pendingEditId: Int? = null,
    homeVm: HomeViewModel = hiltViewModel(),
    sheetVm: ReminderSheetViewModel = hiltViewModel()
) {
    val state by homeVm.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var editReminderId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(pendingEditId) {
        if (pendingEditId != null) {
            sheetVm.loadReminder(pendingEditId)
            showSheet = true
        }
    }

    val filteredReminders = if (state.filterCategoryId == null) state.reminders
    else state.reminders.filter { it.categoryId == state.filterCategoryId }

    val hero = filteredReminders.firstOrNull()
    val rest = filteredReminders.drop(1)

    if (showSheet) {
        ReminderSheet(
            vm = sheetVm,
            onDismiss = { showSheet = false; editReminderId = null },
            onDeleted = { showSheet = false; editReminderId = null }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.reminders.isEmpty()) {
            EmptyState(onAddClick = {
                sheetVm.loadReminder(null)
                showSheet = true
            })
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Header
                item {
                    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 14.dp)) {
                        Text(DateTimeUtil.hebrewDateFull(System.currentTimeMillis()), style = AppTypography.labelSmall, color = Neutral700)
                        Text("תזכורות", style = AppTypography.headlineLarge, color = TextColor)
                    }
                }

                // Category filter chips
                item {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 0.dp)
                            .padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryFilterChip(label = "הכל", selected = state.filterCategoryId == null, onClick = { homeVm.setFilter(null) })
                        state.categories.forEach { cat ->
                            CategoryFilterChip(
                                label = cat.name,
                                dotColor = paletteColor(cat.color),
                                selected = state.filterCategoryId == cat.id,
                                onClick = { homeVm.setFilter(cat.id) }
                            )
                        }
                    }
                }

                // Hero card or filter empty
                if (filteredReminders.isEmpty() && state.filterCategoryId != null) {
                    item {
                        val catName = state.categories.firstOrNull { it.id == state.filterCategoryId }?.name ?: ""
                        FilterEmptyState(categoryName = catName, onShowAll = { homeVm.setFilter(null) })
                    }
                } else if (hero != null) {
                    item {
                        val tone = categoryTone(state.categories.firstOrNull { it.id == hero.categoryId }?.color ?: "accent-500")
                        HeroCard(
                            reminder = hero,
                            tone = tone,
                            onClick = {
                                sheetVm.loadReminder(hero.id)
                                showSheet = true
                            },
                            onDone = { homeVm.markDone(hero) },
                            onPauseResume = { homeVm.togglePause(hero) }
                        )
                    }

                    if (rest.isNotEmpty()) {
                        item {
                            Text(
                                "בהמשך",
                                style = AppTypography.labelMedium,
                                color = Neutral700,
                                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 6.dp)
                            )
                        }

                        items(rest, key = { it.id }) { reminder ->
                            val catColor = state.categories.firstOrNull { it.id == reminder.categoryId }?.color ?: "accent-500"
                            ReminderRow(
                                reminder = reminder,
                                categoryColor = catColor,
                                onClick = {
                                    sheetVm.loadReminder(reminder.id)
                                    showSheet = true
                                },
                                onPauseResume = { homeVm.togglePause(reminder) },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // FAB
        if (state.reminders.isNotEmpty()) {
            FloatingActionButton(
                onClick = {
                    sheetVm.loadReminder(null)
                    showSheet = true
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 100.dp),
                shape = CircleShape,
                containerColor = AccentMain,
                contentColor = Bg
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "תזכורת חדשה", modifier = Modifier.size(26.dp))
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

private fun paletteColor(token: String) = com.example.reminders.ui.theme.paletteColor(token)
