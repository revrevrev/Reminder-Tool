package com.example.reminders.ui.theme

import androidx.compose.ui.graphics.Color

// Background & surface
val Bg = Color(0xFFF5EAD8)
val Surface = Color(0xFFEBDDC5)
val TextColor = Color(0xFF201E1D)

// Neutral ramp
val Neutral100 = Color(0xFFF9F4ED)
val Neutral200 = Color(0xFFEEE7DB)
val Neutral300 = Color(0xFFDCD3C4)
val Neutral400 = Color(0xFFC0B6A5)
val Neutral500 = Color(0xFFA19786)
val Neutral600 = Color(0xFF82796A)
val Neutral700 = Color(0xFF645C50)
val Neutral800 = Color(0xFF474238)
val Neutral900 = Color(0xFF2E2B25)

// Accent (orange) ramp
val Accent100 = Color(0xFFFFF2EB)
val Accent200 = Color(0xFFFFE1D0)
val Accent300 = Color(0xFFFFC6A5)
val Accent400 = Color(0xFFF6A06B)
val Accent500 = Color(0xFFD67F48)
val Accent600 = Color(0xFFB2622D)   // default accent
val AccentMain = Color(0xFFC67139)
val Accent700 = Color(0xFF8C491A)
val Accent800 = Color(0xFF643312)
val Accent900 = Color(0xFF402310)

// Accent-2 (green) ramp
val Accent2_100 = Color(0xFFF0FAE1)
val Accent2_200 = Color(0xFFE1EECC)
val Accent2_300 = Color(0xFFCCDBB2)
val Accent2_400 = Color(0xFFAEBF92)
val Accent2_500 = Color(0xFF8FA073)
val Accent2_600 = Color(0xFF728157)
val Accent2_700 = Color(0xFF56633F)
val Accent2_800 = Color(0xFF3D472B)
val Accent2_900 = Color(0xFF272E1B)

// Nav bar
val NavBarBg = Surface

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
