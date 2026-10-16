package com.example.reminders.ui.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.reminders.data.model.ALL_SOUNDS
import com.example.reminders.data.model.Sound
import com.example.reminders.ui.theme.*
import com.example.reminders.util.SoundPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundPickerSheet(
    title: String,
    currentSound: String,
    previewVolume: Int,
    categoryDefaultSound: String? = null,
    onSoundSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selected by remember(currentSound) { mutableStateOf(currentSound) }

    DisposableEffect(Unit) { onDispose { SoundPlayer.stop() } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Bg,
        shape = SheetShape
    ) {
        Column(modifier = Modifier.padding(bottom = 30.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = AppTypography.headlineSmall, color = TextColor)
                TextButton(onClick = {
                    onSoundSelected(selected)
                    onDismiss()
                }) {
                    Text("בחר", style = AppTypography.labelLarge, color = AccentMain)
                }
            }

            LazyColumn(modifier = Modifier.padding(horizontal = 12.dp)) {
                items(ALL_SOUNDS) { sound ->
                    SoundRow(
                        sound = sound,
                        selected = selected == sound.id,
                        isDefault = categoryDefaultSound == sound.id,
                        onSelect = {
                            selected = sound.id
                            SoundPlayer.preview(context, sound.id, previewVolume)
                        },
                        onPreview = {
                            SoundPlayer.preview(context, sound.id, previewVolume)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SoundRow(
    sound: Sound,
    selected: Boolean,
    isDefault: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    // Outer row: not clickable — separates the selectable area from the play button so
    // tapping the play button does not also fire onSelect (Compose propagates taps to all
    // overlapping clickable regions, so the button must live outside the clickable area).
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(if (selected) Accent100 else Bg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Selectable area (radio + name) — takes up all space except the play button
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onSelect),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Radio dot
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Bg)
                    .border(2.dp, if (selected) AccentMain else Neutral400, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AccentMain)
                    )
                }
            }

            // Name + description + tag
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(sound.name, style = AppTypography.titleSmall, color = TextColor)
                    if (isDefault) {
                        Box(
                            modifier = Modifier
                                .clip(PillShape)
                                .background(Accent200)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("ברירת מחדל", style = AppTypography.labelSmall, color = Accent800)
                        }
                    }
                }
                Text(sound.description, style = AppTypography.labelMedium, color = Neutral700)
            }
        }

        // Play button — sibling of the selectable area, not nested inside it
        IconButton(
            onClick = onPreview,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Surface)
        ) {
            Icon(Icons.Outlined.PlayArrow, contentDescription = "נגן", tint = Neutral700, modifier = Modifier.size(18.dp))
        }
    }
}

