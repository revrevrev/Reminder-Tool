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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reminders.data.model.Category
import com.example.reminders.data.model.ReminderType
import com.example.reminders.ui.component.*
import com.example.reminders.ui.theme.*
import com.example.reminders.util.DateTimeUtil
import com.example.reminders.viewmodel.ReminderDraft
import com.example.reminders.viewmodel.ReminderSheetViewModel
import com.example.reminders.viewmodel.isValid

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

    // Autofocus name field when sheet opens
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

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

    // Full-screen page: slides up from bottom (ModalBottomSheet configured full-screen)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Bg,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
        dragHandle = null,
        modifier = Modifier.fillMaxHeight(),
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 30.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header row: [Close] [Title] [Trash (edit)] [Save]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = "סגור",
                        modifier = Modifier.size(16.dp), tint = Neutral700)
                }

                Text(
                    text = if (isEdit) "עריכת תזכורת" else "תזכורת חדשה",
                    style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Neutral700,
                    modifier = Modifier.weight(1f)
                )

                if (isEdit) {
                    IconButton(onClick = { vm.delete { onDeleted() } }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "מחק",
                            tint = Accent700, modifier = Modifier.size(18.dp))
                    }
                }

                Button(
                    onClick = { vm.save { onDismiss() } },
                    enabled = draft.isValid(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 9.dp),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentMain, contentColor = Bg,
                        disabledContainerColor = AccentMain.copy(alpha = 0.45f),
                        disabledContentColor = Bg.copy(alpha = 0.45f)
                    )
                ) {
                    Text("שמירה", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // 1. Name field (FIRST — sits above keyboard when it opens)
            Column {
                Text("שם התזכורת", style = AppTypography.labelMedium, color = Neutral700)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = { vm.updateName(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 44.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    textStyle = AppTypography.bodyLarge.copy(fontSize = 16.sp),
                    shape = PillShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Bg, unfocusedContainerColor = Bg,
                        focusedBorderColor = AccentMain, unfocusedBorderColor = Neutral300
                    )
                )
            }

            // 2. Type selector — compact pills, no captions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypePill(
                    title = "חד-פעמית",
                    icon = Icons.Outlined.CalendarToday,
                    selected = draft.type == ReminderType.ONCE,
                    accentBg = Accent200, accentFg = Accent900, accentRing = Accent600,
                    onSelect = { vm.updateType(ReminderType.ONCE) },
                    modifier = Modifier.weight(1f)
                )
                TypePill(
                    title = "שבועית",
                    icon = Icons.Outlined.Repeat,
                    selected = draft.type == ReminderType.WEEKLY,
                    accentBg = Accent2_200, accentFg = Accent2_900, accentRing = Accent2_600,
                    onSelect = { vm.updateType(ReminderType.WEEKLY) },
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Category chips (no sound/volume helper line)
            CategorySelectorSection(
                categories = categories,
                selectedId = draft.categoryId,
                onSelect = { vm.updateCategory(it) }
            )

            // 4. Weekday picker (weekly only)
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

            // 5. Date + time row (min-height 42)
            DateTimeRow(
                draft = draft,
                onDateChange = { vm.updateDate(it) },
                onTimeChange = { vm.updateTime(it) }
            )

            if (draft.type == ReminderType.ONCE && DateTimeUtil.isInPast(draft.date, draft.time)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Outlined.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "לא ניתן לשמור תזכורת לזמן שעבר",
                        style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // 6. Summary — plain text (no pill background)
            val summary = DateTimeUtil.summaryPill(draft.type, draft.date, draft.day, draft.time)
            if (summary.isNotEmpty()) {
                Text(summary, style = AppTypography.bodySmall, color = Accent2_800)
            }

            // 7. Sound override card (edit only)
            if (isEdit) {
                SoundOverrideCard(
                    draft = draft,
                    categories = categories,
                    onToggle = { vm.setSoundOverride(it) },
                    onSoundPickerOpen = { showSoundPicker = true },
                    onVolumeChange = { vm.updateVolume(it) },
                    onContinuousChange = { vm.updateContinuous(it) }
                )
            }

            // 8. Notes
            if (!showNotes) {
                TextButton(onClick = { showNotes = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = null,
                        modifier = Modifier.size(16.dp))
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
        }
    }
}

@Composable
private fun CategorySelectorSection(
    categories: List<Category>,
    selectedId: Int?,
    onSelect: (Int) -> Unit
) {
    Column {
        Text("קטגוריה", style = AppTypography.labelMedium, color = Neutral700)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { cat ->
                CategorySelectorChip(category = cat, selected = cat.id == selectedId, onClick = { onSelect(cat.id) })
            }
        }
    }
}

@Composable
private fun TypePill(
    title: String,
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
    val borderMod = if (selected) Modifier.border(2.dp, accentRing, PillShape) else Modifier

    Row(
        modifier = modifier
            .clip(PillShape)
            .background(bg)
            .then(borderMod)
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null,
            modifier = Modifier.size(16.dp), tint = fg)
        Spacer(Modifier.width(8.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = fg)
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
            val dateParts = draft.date.split("-")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 42.dp)
                    .clip(PillShape)
                    .background(Surface)
                    .clickable {
                        val y = dateParts[0].toInt()
                        val m = dateParts[1].toInt() - 1
                        val d = dateParts[2].toInt()
                        DatePickerDialog(context, { _, yr, mo, dy ->
                            onDateChange("%04d-%02d-%02d".format(yr, mo + 1, dy))
                        }, y, m, d).show()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null,
                        modifier = Modifier.size(16.dp), tint = Neutral600)
                    Text(draft.date, style = AppTypography.bodyMedium, color = TextColor)
                }
            }
        }

        val timeParts = draft.time.split(":")
        Box(
            modifier = Modifier
                .weight(if (draft.type == ReminderType.ONCE) 1f else 2f)
                .heightIn(min = 42.dp)
                .clip(PillShape)
                .background(Surface)
                .clickable {
                    val h = timeParts[0].toInt()
                    val m = timeParts[1].toInt()
                    TimePickerDialog(context, { _, hr, mn ->
                        onTimeChange("%02d:%02d".format(hr, mn))
                    }, h, m, true).show()
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.AccessTime, contentDescription = null,
                    modifier = Modifier.size(16.dp), tint = Neutral600)
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
    onVolumeChange: (Int) -> Unit,
    onContinuousChange: (Boolean) -> Unit
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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.MusicNote, contentDescription = null,
                    modifier = Modifier.size(20.dp), tint = Neutral700)
                Column {
                    Text("צליל ועוצמה", style = AppTypography.titleSmall, color = TextColor)
                    val subtitle = if (!draft.soundOverrideEnabled && cat != null) {
                        val continuousSuffix = if (cat.continuous) " · רציף" else ""
                        "לפי ${cat.name}: ${cat.sound} · ${cat.volume}%$continuousSuffix"
                    } else "מותאם לתזכורת זו"
                    Text(subtitle, style = AppTypography.labelMedium, color = Neutral700,
                        maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            val dEff = if (cat != null)
                Triple(draft.sound ?: cat.sound, draft.volume ?: cat.volume, draft.continuous ?: cat.continuous)
            else Triple(draft.sound ?: "chime", draft.volume ?: 70, draft.continuous ?: false)

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
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(dEff.first, style = AppTypography.titleSmall, color = TextColor)
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = null,
                        modifier = Modifier.size(16.dp), tint = Neutral600)
                }
            }

            val vol = dEff.second
            Column {
                Text("עוצמה · $vol%", style = AppTypography.labelMedium, color = Neutral700)
                Slider(
                    value = vol.toFloat(),
                    onValueChange = { onVolumeChange(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = AccentMain, activeTrackColor = AccentMain)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PillShape)
                    .background(Bg)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Repeat, contentDescription = null,
                        modifier = Modifier.size(18.dp), tint = Neutral600)
                    Text("צלצול רציף", style = AppTypography.bodyMedium, color = Neutral700)
                }
                Switch(
                    checked = dEff.third,
                    onCheckedChange = { onContinuousChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Bg, checkedTrackColor = AccentMain,
                        uncheckedThumbColor = Bg, uncheckedTrackColor = Neutral400
                    )
                )
            }
        }
    }
}
