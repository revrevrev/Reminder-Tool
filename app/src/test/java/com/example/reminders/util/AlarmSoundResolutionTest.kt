package com.example.reminders.util

import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression tests for defect 13: changing the default sound for a category did not affect
 * reminders from that category when the alarm fired.
 *
 * Root cause: AlarmActivity.onCreate() called vm.effectiveSound() synchronously right after
 * vm.loadReminder(), before the async coroutine completed. The ViewModel state was still
 * empty (reminder = null), so effectiveSound() always returned the hardcoded fallback "chime"
 * regardless of what category or reminder specified.
 *
 * Fix requirement:
 *   1. Extract sound resolution to a pure function resolveAlarmSound(reminder, category).
 *   2. AlarmActivity must not start the alarm sound until the ViewModel state has loaded
 *      (reminder + category fetched from the database).
 *
 * These tests verify the pure resolution logic. The timing fix is in AlarmActivity.
 */
class AlarmSoundResolutionTest {

    private val category = Category(id = 42, name = "עבודה", color = "accent-500", sound = "bell", volume = 80)
    private val reminderNoOverride = Reminder(id = 1, name = "פגישה", type = ReminderType.ONCE, time = "09:00", categoryId = 42, sound = null)
    private val reminderWithOverride = Reminder(id = 2, name = "אחר", type = ReminderType.ONCE, time = "10:00", categoryId = 42, sound = "marimba")

    @Test
    fun `resolveAlarmSound returns category sound when reminder has no sound override`() {
        // FAILS before fix: resolveAlarmSound does not exist yet.
        assertEquals(
            "reminder with null sound must inherit category sound",
            "bell",
            NotificationUtil.resolveAlarmSound(reminderNoOverride, category)
        )
    }

    @Test
    fun `resolveAlarmSound returns reminder sound when reminder has explicit override`() {
        assertEquals(
            "reminder with explicit sound must use its own sound, not category",
            "marimba",
            NotificationUtil.resolveAlarmSound(reminderWithOverride, category)
        )
    }

    @Test
    fun `resolveAlarmSound reflects updated category sound for reminder with no override`() {
        // This is the exact defect scenario: user changed the category sound from "chime" to "bell".
        // When the alarm fires, it must use the category's CURRENT sound.
        val updatedCategory = category.copy(sound = "birds")
        assertEquals(
            "after category sound changes, alarm must use the new sound",
            "birds",
            NotificationUtil.resolveAlarmSound(reminderNoOverride, updatedCategory)
        )
    }

    @Test
    fun `resolveAlarmSound falls back to chime when both reminder and category are null`() {
        assertEquals("chime", NotificationUtil.resolveAlarmSound(null, null))
    }

    @Test
    fun `resolveAlarmSound falls back to category sound when reminder is null`() {
        assertEquals("bell", NotificationUtil.resolveAlarmSound(null, category))
    }
}
