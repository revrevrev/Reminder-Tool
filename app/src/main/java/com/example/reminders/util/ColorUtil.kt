package com.example.reminders.util

import androidx.compose.ui.graphics.Color
import com.example.reminders.ui.theme.paletteColor as themePaletteColor

// Re-export for convenience from util package
fun paletteColor(token: String): Color = themePaletteColor(token)
