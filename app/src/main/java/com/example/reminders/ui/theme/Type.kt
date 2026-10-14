package com.example.reminders.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system fonts as fallback; replace with bundled TTFs for production.
// Place suez_one.ttf and rubik_regular/medium/semibold/bold.ttf in res/font/
// then un-comment the Font() lines and delete the system fallback lines.

val SuezOneFamily: FontFamily = FontFamily.Serif    // replace with bundled Suez One
val RubikFamily: FontFamily = FontFamily.SansSerif  // replace with bundled Rubik

// Uncomment when TTF files are added to res/font/:
// val SuezOneFamily = FontFamily(Font(R.font.suez_one, FontWeight.Normal))
// val RubikFamily = FontFamily(
//     Font(R.font.rubik_regular, FontWeight.Normal),
//     Font(R.font.rubik_medium, FontWeight.Medium),
//     Font(R.font.rubik_semibold, FontWeight.SemiBold),
//     Font(R.font.rubik_bold, FontWeight.Bold)
// )

val AppTypography = Typography(
    displayLarge  = TextStyle(fontFamily = SuezOneFamily, fontSize = 76.sp),
    displayMedium = TextStyle(fontFamily = SuezOneFamily, fontSize = 32.sp),
    displaySmall  = TextStyle(fontFamily = SuezOneFamily, fontSize = 19.sp),
    headlineLarge  = TextStyle(fontFamily = SuezOneFamily, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = SuezOneFamily, fontSize = 24.sp),
    headlineSmall  = TextStyle(fontFamily = SuezOneFamily, fontSize = 20.sp),
    titleLarge  = TextStyle(fontFamily = RubikFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = RubikFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    titleSmall  = TextStyle(fontFamily = RubikFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    bodyLarge  = TextStyle(fontFamily = RubikFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    bodyMedium = TextStyle(fontFamily = RubikFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall  = TextStyle(fontFamily = RubikFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp),
    labelLarge  = TextStyle(fontFamily = RubikFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
    labelMedium = TextStyle(fontFamily = RubikFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
    labelSmall  = TextStyle(fontFamily = RubikFamily, fontWeight = FontWeight.Normal, fontSize = 11.sp)
)
