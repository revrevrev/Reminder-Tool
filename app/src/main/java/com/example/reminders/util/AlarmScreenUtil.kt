package com.example.reminders.util

import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder

data class AlarmDisplayItems(
    val name: String,
    val categoryName: String?,
    val notes: String?
)

fun buildAlarmDisplayItems(reminder: Reminder, category: Category?): AlarmDisplayItems =
    AlarmDisplayItems(
        name = reminder.name,
        categoryName = category?.name,
        notes = reminder.notes
    )
