package com.example.reminders.ui.sheet

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.reminders.data.model.ALL_SOUNDS
import com.example.reminders.data.model.Category
import com.example.reminders.data.model.ReminderType
import com.example.reminders.ui.component.*
import com.example.reminders.ui.theme.*
import com.example.reminders.util.DateTimeUtil
import com.example.reminders.viewmodel.ReminderDraft
import com.example.reminders.viewmodel.ReminderSheetViewModel
import com.example.reminders.viewmodel.isValid
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSheet(
    vm: ReminderSheetViewModel,
    onDismiss: () -> Unit,
    onDeleted: () -> Unit = {}
) {
    val draft by vm.draft.collectAsState()
    val categories by vm.categories.collectAsState()
    val isEdit = draft.id != 0

    var showSoundPicker by remember { mutableStateOf(false) }
    var showNotes by remember { mutableStateOf(draft.notes != null) }

    if (showSoundPicker) {
        val cat = categories.firstOrNull { it.id == draft.categoryId }
        SoundPickerSheet(
            title = "בחירת צליל",
            currentSound = draft.sound ?: cat?.sound ?: "chime",
            previewVolume = draft.volume ?: cat?.volume ?: 70,
            categoryDefaultSound = cat?.sound,
            onSoundSelected = { vm.updateSound(it) },
            onDismiss = { showSoundPicker = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Bg,
        shape = SheetShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEdit) {
                    IconButton(onClick = { vm.delete { onDeleted() } }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "מחק", tint = Accent700)
                    }
                } else Spacer(Modifier.size(48.dp))

                Text(
                    text = if (isEdit) "עריכת תזכורת" else "תזכורת חדשה",
                    style = AppTypography.headlineSmall,
                    color = TextColor
                )

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "סגור", tint = Neutral700)
                }
            }

            // Type tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TypeTile(
                    title = "חד-פעמית",
                    caption = "עוברת לארכיון בסיום",
                    icon = Icons.Outlined.CalendarToday,
                    selected = draft.type == ReminderType.ONCE,
                    accentBg = Accent200, accentFg = Accent900, accentRing = Accent600,
                    onSelect = { vm.updateType(ReminderType.ONCE) },
                    modifier = Modifier.weight(1f)
                )
                TypeTile(
                    title = "שבועית",
                    caption = "חוזרת ביום קבוע",
                    icon = Icons.Outlined.Repeat,
                    selected = draft.type == ReminderType.WEEKLY,
                    accentBg = Accent2_200, accentFg = Accent2_900, accentRing = Accent2_600,
                    onSelect = { vm.updateType(ReminderType.WEEKLY) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Name field
            Column {
                Text("שם התזכורת", style = AppTypography.labelMedium, color = Neutral700)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = { vm.updateName(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("למשל: להתקשר לאמא", color = Neutral500) },
                    shape = PillShape,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Bg, unfocusedContainerColor = Bg,
                        focusedBorderColor = AccentMain, unfocusedBorderColor = Neutral300
                    )
                )
            }

            // Category chips
            CategorySelectorSection(
                categories = categories,
                selectedId = draft.categoryId,
                isNew = !isEdit,
                draft = draft,
                onSelect = { vm.updateCategory(it) }
            )

            // Weekly day picker
            if (draft.type == ReminderType.WEEKLY) {
                Column {
                    Text("יום בשבוע", style = AppTypography.labelMedium, color = Neutral700)
                    Spacer(Modifier.height(8.dp))
                    WeekdayPicker(
                        selectedDay = draft.day,
                        onDaySelected = { vm.updateDay(it) }
                    )
                }
            }

            // Date + time row
            DateTimeRow(draft = draft, onDateChange = { vm.updateDate(it) }, onTimeChange = { vm.updateTime(it) })

            // Summary pill
            val summary = DateTimeUtil.summaryPill(draft.type, draft.date, draft.day, draft.time)
            if (summary.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(Accent2_100)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(summary, style = AppTypography.bodySmall, color = Accent2_800)
                }
            }

            // Sound override (edit only)
            if (isEdit) {
                SoundOverrideCard(
                    draft = draft,
                    categories = categories,
                    onToggle = { vm.setSoundOverride(it) },
                    onSoundPickerOpen = { showSoundPicker = true },
                    onVolumeChange = { vm.updateVolume(it) }
                )
            }

            // Notes
            if (!showNotes) {
                TextButton(onClick = { showNotes = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+ הוספת הערה", style = AppTypography.labelLarge, color = Neutral700)
                }
            } else {
                Column {
                    Text("הערות", style = AppTypography.labelMedium, color = Neutral700)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = draft.notes ?: "",
                        onValueChange = { vm.updateNotes(it.takeIf { it.isNotBlank() }) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 70.dp),
                        placeholder = { Text("פרטים נוספים…", color = Neutral500) },
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Bg, unfocusedContainerColor = Bg,
                            focusedBorderColor = AccentMain, unfocusedBorderColor = Neutral300
                        )
                    )
                }
            }

            // Validation helper
            val validationMsg = when {
                draft.name.isBlank() && draft.categoryId == null -> "יש להזין שם ולבחור קטגוריה"
                draft.name.isBlank() -> "יש להזין שם"
                draft.categoryId == null -> "יש לבחור קטגוריה"
                else -> null
            }

            // Save button
            Button(
                onClick = { vm.save { onDismiss() } },
                enabled = draft.isValid(),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 15.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentMain, contentColor = Bg,
                    disabledContainerColor = AccentMain.copy(alpha = 0.45f), disabledContentColor = Bg.copy(alpha = 0.45f)
                )
            ) {
                Text("שמירה", style = AppTypography.titleMedium)
            }

            if (validationMsg != null) {
                Text(validationMsg, style = AppTypography.labelMedium, color = Neutral700)
            }
        }
    }
}

@Composable
private fun CategorySelectorSection(
    categories: List<Category>,
    selectedId: Int?,
    isNew: Boolean,
    draft: ReminderDraft,
    onSelect: (Int) -> Unit
) {
    val selectedCat = categories.firstOrNull { it.id == selectedId }
    Column {
        Text("קטגוריה", style = AppTypography.labelMedium, color = Neutral700)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { cat ->
                CategorySelectorChip(category = cat, selected = cat.id == selectedId, onClick = { onSelect(cat.id) })
            }
        }
        if (isNew) {
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.MusicNote, contentDescription = null, modifier = Modifier.size(12.dp), tint = Neutral700)
                Text(
                    text = if (selectedCat == null) "הצליל והעוצמה נקבעים לפי הקטגוריה"
                    else "יופעל עם ${selectedCat.sound} · ${selectedCat.volume}% (ברירת המחדל של ${selectedCat.name})",
                    style = AppTypography.labelMedium, color = Neutral700
                )
            }
        }
    }
}

@Composable
private fun TypeTile(
    title: String, caption: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    accentBg: androidx.compose.ui.graphics.Color,
    accentFg: androidx.compose.ui.graphics.Color,
    accentRing: androidx.compose.ui.graphics.Color,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) accentBg else Surface
    val fg = if (selected) accentFg else TextColor
    val borderMod = if (selected) Modifier.border(2.dp, accentRing, TypeTileShape) else Modifier

    Column(
        modifier = modifier
            .clip(TypeTileShape)
            .background(bg)
            .then(borderMod)
            .clickable(onClick = onSelect)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = fg)
        Text(title, style = AppTypography.titleLarge, color = fg)
        Text(caption, style = AppTypography.labelMedium, color = fg.copy(alpha = 0.7f))
    }
}

@Composable
private fun DateTimeRow(
    draft: ReminderDraft,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (draft.type == ReminderType.ONCE) {
            // Date picker trigger
            val dateParts = draft.date.split("-")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(PillShape)
                    .background(Surface)
                    .clickable {
                        val y = dateParts[0].toInt(); val m = dateParts[1].toInt() - 1; val d = dateParts[2].toInt()
                        DatePickerDialog(context, { _, yr, mo, dy ->
                            onDateChange("%04d-%02d-%02d".format(yr, mo + 1, dy))
                        }, y, m, d).show()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = Neutral600)
                    Text(draft.date, style = AppTypography.bodyMedium, color = TextColor)
                }
            }
        }

        // Time picker trigger
        val timeParts = draft.time.split(":")
        Box(
            modifier = Modifier
                .weight(if (draft.type == ReminderType.ONCE) 1f else 2f)
                .clip(PillShape)
                .background(Surface)
                .clickable {
                    val h = timeParts[0].toInt(); val m = timeParts[1].toInt()
                    TimePickerDialog(context, { _, hr, mn ->
                        onTimeChange("%02d:%02d".format(hr, mn))
                    }, h, m, true).show()
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = Neutral600)
                Text(draft.time, style = AppTypography.bodyMedium, color = TextColor)
            }
        }
    }
}

@Composable
private fun SoundOverrideCard(
    draft: ReminderDraft,
    categories: List<Category>,
    onToggle: (Boolean) -> Unit,
    onSoundPickerOpen: () -> Unit,
    onVolumeChange: (Int) -> Unit
) {
    val cat = categories.firstOrNull { it.id == draft.categoryId }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.MusicNote, contentDescription = null, modifier = Modifier.size(20.dp), tint = Neutral700)
                Column {
                    Text("צליל ועוצמה", style = AppTypography.titleSmall, color = TextColor)
                    val subtitle = if (!draft.soundOverrideEnabled && cat != null)
                        "לפי ${cat.name}: ${cat.sound} · ${cat.volume}%"
                    else "מותאם לתזכורת זו"
                    Text(subtitle, style = AppTypography.labelMedium, color = Neutral700)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("התאמה אישית", style = AppTypography.labelMedium, color = Neutral700)
                Switch(
                    checked = draft.soundOverrideEnabled,
                    onCheckedChange = { onToggle(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Bg, checkedTrackColor = AccentMain,
                        uncheckedThumbColor = Bg, uncheckedTrackColor = Neutral400
                    )
                )
            }
        }

        if (draft.soundOverrideEnabled) {
            // Sound selector pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PillShape)
                    .background(Bg)
                    .clickable(onClick = onSoundPickerOpen)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("צליל", style = AppTypography.bodyMedium, color = Neutral700)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(draft.sound ?: cat?.sound ?: "chime", style = AppTypography.titleSmall, color = TextColor)
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = null, modifier = Modifier.size(16.dp), tint = Neutral600)
                }
            }

            // Volume slider
            val vol = draft.volume ?: cat?.volume ?: 70
            Column {
                Text("עוצמה · $vol%", style = AppTypography.labelMedium, color = Neutral700)
                Slider(
                    value = vol.toFloat(),
                    onValueChange = { onVolumeChange(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = AccentMain, activeTrackColor = AccentMain)
                )
            }
        }
    }
}
