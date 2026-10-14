package com.example.reminders.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val AppColorScheme = lightColorScheme(
    primary = AccentMain,
    onPrimary = Bg,
    primaryContainer = Accent200,
    onPrimaryContainer = Accent800,
    secondary = Accent2_600,
    onSecondary = Bg,
    secondaryContainer = Accent2_200,
    onSecondaryContainer = Accent2_800,
    background = Bg,
    onBackground = TextColor,
    surface = Surface,
    onSurface = TextColor,
    surfaceVariant = Neutral200,
    onSurfaceVariant = Neutral700,
    outline = Neutral400,
    error = Accent700,
    onError = Bg
)

@Composable
fun RemindersTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = AppColorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
