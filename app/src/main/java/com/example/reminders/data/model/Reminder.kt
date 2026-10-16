package com.example.reminders.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
enum class ReminderType { ONCE, WEEKLY }

@Serializable
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: ReminderType,
    val date: String? = null,        // "YYYY-MM-DD" for ONCE
    val day: Int? = null,             // 0=Sun .. 6=Sat for WEEKLY
    val time: String,                 // "HH:MM"
    val categoryId: Int,
    val sound: String? = null,        // null = inherit from category
    val volume: Int? = null,          // null = inherit from category
    val continuous: Boolean? = null,  // null = inherit from category
    val notes: String? = null,
    val paused: Boolean = false,
    val snoozeUntil: Long? = null,    // epoch millis
    val skipUntil: Long? = null,      // epoch millis (weekly: skip until after this)
    val archived: Boolean = false,
    val archivedAt: Long? = null      // epoch millis
)
