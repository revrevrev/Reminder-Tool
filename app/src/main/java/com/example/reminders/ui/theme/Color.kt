package com.example.reminders.ui.theme

import androidx.compose.ui.graphics.Color

// Paper theme — all hex values are sRGB conversions of the OKLCH ramp
// defined in CHANGES.md / the design system.

// Background & surface
val Bg = Color(0xFFFFFFFF)
val Surface = Color(0xFFF2F3F5)
val TextColor = Color(0xFF151618)

// Neutral ramp (hue 260, chroma .008, lightness steps .975/.93/.87/.78/.67/.56/.46/.36/.27)
val Neutral100 = Color(0xFFF5F7F9)
val Neutral200 = Color(0xFFE7E8EA)
val Neutral300 = Color(0xFFD2D4D8)
val Neutral400 = Color(0xFFB5B7BB)
val Neutral500 = Color(0xFF92959A)
val Neutral600 = Color(0xFF727579)
val Neutral700 = Color(0xFF55585D)
val Neutral800 = Color(0xFF3B3D41)
val Neutral900 = Color(0xFF24272A)

// Accent (blue) ramp — hue 262, chroma .15
val Accent100 = Color(0xFFE4F8FF)
val Accent200 = Color(0xFFD5E9FF)
val Accent300 = Color(0xFFAFD5FF)
val Accent400 = Color(0xFF93B8FB)
val Accent500 = Color(0xFF6193F1)
val Accent600 = Color(0xFF4170CB)
val AccentMain = Accent600   // primary accent = accent-600
val Accent700 = Color(0xFF2552AA)
val Accent800 = Color(0xFF08348A)
val Accent900 = Color(0xFF00186E)

// Accent-2 (green) ramp — hue 160, chroma .09
val Accent2_100 = Color(0xFFE5FEEF)
val Accent2_200 = Color(0xFFD7EFE1)
val Accent2_300 = Color(0xFFB1E2C6)
val Accent2_400 = Color(0xFF95C4AA)
val Accent2_500 = Color(0xFF60A682)
val Accent2_600 = Color(0xFF3E8562)
val Accent2_700 = Color(0xFF1D6746)
val Accent2_800 = Color(0xFF004B2C)
val Accent2_900 = Color(0xFF003316)

// Nav bar
val NavBarBg = Bg

fun paletteColor(token: String): Color = when (token) {
    "accent-100" -> Accent100; "accent-200" -> Accent200; "accent-300" -> Accent300
    "accent-400" -> Accent400; "accent-500" -> Accent500; "accent-600" -> Accent600
    "accent-700" -> Accent700; "accent-800" -> Accent800; "accent-900" -> Accent900
    "accent-2-100" -> Accent2_100; "accent-2-200" -> Accent2_200; "accent-2-300" -> Accent2_300
    "accent-2-400" -> Accent2_400; "accent-2-500" -> Accent2_500; "accent-2-600" -> Accent2_600
    "accent-2-700" -> Accent2_700; "accent-2-800" -> Accent2_800; "accent-2-900" -> Accent2_900
    "neutral-100" -> Neutral100; "neutral-200" -> Neutral200; "neutral-300" -> Neutral300
    "neutral-400" -> Neutral400; "neutral-500" -> Neutral500; "neutral-600" -> Neutral600
    "neutral-700" -> Neutral700; "neutral-800" -> Neutral800; "neutral-900" -> Neutral900
    else -> AccentMain
}
