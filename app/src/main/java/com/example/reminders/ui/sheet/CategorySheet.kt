package com.example.reminders.ui.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.reminders.ui.component.CategorySelectorChip
import com.example.reminders.ui.component.ColorSwatchRow
import com.example.reminders.ui.theme.*
import com.example.reminders.util.paletteColor
import com.example.reminders.viewmodel.CategorySheetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySheet(
    vm: CategorySheetViewModel,
    onDismiss: () -> Unit
) {
    val draft by vm.draft.collectAsState()
    val deleteMode by vm.deleteMode.collectAsState()
    val moveTarget by vm.moveTarget.collectAsState()
    val canDelete by vm.canDelete.collectAsState()
    val allCategories by vm.allCategories.collectAsState()
    val isEdit = draft.id != 0

    var showSoundPicker by remember { mutableStateOf(false) }

    if (showSoundPicker) {
        SoundPickerSheet(
            title = "צליל לקטגוריה ${draft.name}",
            currentSound = draft.sound,
            previewVolume = draft.volume,
            onSoundSelected = { vm.updateSound(it) },
            onDismiss = { showSoundPicker = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Bg,
        shape = SheetShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live color circle + title
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(paletteColor(draft.color))
                    )
                    Text(
                        text = if (!isEdit) "קטגוריה חדשה" else draft.name,
                        style = AppTypography.headlineSmall, color = TextColor
                    )
                }
                Row {
                    if (isEdit && canDelete && !deleteMode) {
                        IconButton(onClick = { vm.enterDeleteMode() }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "מחק", tint = Accent700)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, contentDescription = "סגור", tint = Neutral700)
                    }
                }
            }

            if (deleteMode) {
                // Delete confirmation panel
                DeletePanel(
                    categoryName = draft.name,
                    otherCategories = allCategories.filter { it.id != draft.id },
                    moveTarget = moveTarget,
                    onTargetSelect = { vm.setMoveTarget(it) },
                    onCancel = { vm.exitDeleteMode() },
                    onConfirm = { vm.deleteAndMove { onDismiss() } }
                )
            } else {
                // Name field
                Column {
                    Text("שם הקטגוריה", style = AppTypography.labelMedium, color = Neutral700)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = draft.name,
                        onValueChange = { vm.updateName(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("למשל: עבודה", color = Neutral500) },
                        shape = PillShape,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Bg, unfocusedContainerColor = Bg,
                            focusedBorderColor = AccentMain, unfocusedBorderColor = Neutral300
                        )
                    )
                }

                // Color swatches
                Column {
                    Text("צבע", style = AppTypography.labelMedium, color = Neutral700)
                    Spacer(Modifier.height(8.dp))
                    ColorSwatchRow(
                        selectedColor = draft.color,
                        onColorSelected = { vm.updateColor(it) }
                    )
                }

                // Sound row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(PillShape)
                        .background(Surface)
                        .clickable { showSoundPicker = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp), tint = Neutral600)
                        Text("צליל ברירת מחדל", style = AppTypography.bodyMedium, color = TextColor)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(draft.sound, style = AppTypography.titleSmall, color = TextColor)
                        Icon(Icons.Outlined.ChevronLeft, contentDescription = null, modifier = Modifier.size(16.dp), tint = Neutral600)
                    }
                }

                // Volume slider
                Column {
                    Text("עוצמת ברירת מחדל · ${draft.volume}%", style = AppTypography.labelMedium, color = Neutral700)
                    Slider(
                        value = draft.volume.toFloat(),
                        onValueChange = { vm.updateVolume(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = AccentMain, activeTrackColor = AccentMain)
                    )
                }

                // Continuous ring toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(PillShape)
                        .background(Surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Repeat, contentDescription = null, modifier = Modifier.size(18.dp), tint = Neutral600)
                        Text("צלצול רציף", style = AppTypography.bodyMedium, color = TextColor)
                    }
                    Switch(
                        checked = draft.continuous,
                        onCheckedChange = { vm.updateContinuous(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Bg, checkedTrackColor = AccentMain,
                            uncheckedThumbColor = Bg, uncheckedTrackColor = Neutral400
                        )
                    )
                }

                // Save button
                Button(
                    onClick = { vm.save { onDismiss() } },
                    enabled = draft.name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 15.dp),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentMain, contentColor = Bg,
                        disabledContainerColor = AccentMain.copy(alpha = 0.45f)
                    )
                ) {
                    Text(if (isEdit) "שמירה" else "יצירת קטגוריה", style = AppTypography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun DeletePanel(
    categoryName: String,
    otherCategories: List<com.example.reminders.data.model.Category>,
    moveTarget: Int?,
    onTargetSelect: (Int) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Accent100)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("למחוק את \u201E$categoryName\u201D?", style = AppTypography.titleMedium, color = TextColor)

        if (otherCategories.isNotEmpty()) {
            Text("בחרו לאן להעביר את התזכורות:", style = AppTypography.bodyMedium, color = Neutral700)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                otherCategories.forEach { cat ->
                    CategorySelectorChip(
                        category = cat,
                        selected = moveTarget == cat.id,
                        onClick = { onTargetSelect(cat.id) }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("ביטול")
            }
            Button(
                onClick = onConfirm,
                enabled = otherCategories.isEmpty() || moveTarget != null,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Accent700)
            ) {
                Text(if (otherCategories.isNotEmpty()) "העברה ומחיקה" else "מחיקה", color = Bg)
            }
        }
    }
}
