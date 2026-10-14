package com.example.reminders.util

import androidx.compose.ui.graphics.Color
import com.example.reminders.ui.theme.Bg
import com.example.reminders.ui.theme.paletteColor

data class CategoryTone(
    val base: Color,
    val fg: Color,
    val c1: Color,
    val c2: Color,
    val btnFg: Color,
    val pauseFg: Color,
    val icon: Color
)

fun categoryTone(colorToken: String): CategoryTone {
    val parts = colorToken.split("-")
    val step = parts.last().toIntOrNull() ?: 500
    val family = parts.dropLast(1).joinToString("-")
    val light = step <= 400

    val base = paletteColor(colorToken)
    val fg = if (light) paletteColor("$family-900") else Bg
    val c1Step = (step - 100).coerceAtLeast(100)
    val c2Step = (step + 100).coerceAtMost(900)
    val c1 = paletteColor("$family-$c1Step")
    val c2 = paletteColor("$family-$c2Step")
    val btnFg = paletteColor("$family-800")
    val icon = paletteColor("$family-700")

    return CategoryTone(base = base, fg = fg, c1 = c1, c2 = c2, btnFg = btnFg, pauseFg = fg, icon = icon)
}
