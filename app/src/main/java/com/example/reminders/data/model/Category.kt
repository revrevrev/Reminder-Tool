package com.example.reminders.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: String,      // palette token e.g. "accent-500"
    val sound: String,      // soundId
    val volume: Int         // 0–100
)

// All 6 available sounds
val ALL_SOUNDS = listOf(
    Sound("chime",   "צלצול",    "רך ובהיר"),
    Sound("bell",    "פעמון",    "עמוק ומהדהד"),
    Sound("birds",   "ציוצים",   "עולה ויורד"),
    Sound("pulse",   "פעימה",    "רכה עדין להשכמה"),
    Sound("marimba", "מרימבה",   "שלושה צלילים"),
    Sound("beep",    "צפצוף",    "קלאסי וחד")
)

data class Sound(val id: String, val name: String, val description: String)

// 8 palette colors available for categories
val CATEGORY_COLORS = listOf(
    "accent-500", "accent-2-500", "accent-300", "neutral-600",
    "accent-2-300", "accent-700", "accent-2-700", "neutral-400"
)
