package com.example.reminders.util

import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Regression test for defect 20: the alarm screen (full-screen lock-screen view)
 * must display the category name alongside the reminder name.
 *
 * Before fix: buildAlarmDisplayItems does not exist; AlarmScreen only rendered
 *             reminder.name and reminder.notes — category was silently dropped.
 * After fix:  buildAlarmDisplayItems exists and exposes categoryName so the
 *             screen can render it.
 */
class AlarmScreenCategoryDisplayTest {

    private val category = Category(id = 3, name = "בריאות", color = "accent-500", sound = "chime", volume = 70)
    private val reminder = Reminder(id = 7, name = "לקחת תרופה", type = ReminderType.ONCE, time = "08:00", categoryId = 3)

    @Test
    fun `alarm display items include the category name when category is present`() {
        val items = buildAlarmDisplayItems(reminder, category)
        assertEquals(
            "Category name must be included in alarm screen display items",
            category.name,
            items.categoryName
        )
    }

    @Test
    fun `alarm display items always include the reminder name`() {
        val items = buildAlarmDisplayItems(reminder, category)
        assertEquals(reminder.name, items.name)
    }

    @Test
    fun `alarm display items have null categoryName when no category is loaded`() {
        val items = buildAlarmDisplayItems(reminder, null)
        assertNull("categoryName must be null when no category is available", items.categoryName)
    }

    @Test
    fun `alarm display items expose notes when present`() {
        val withNotes = reminder.copy(notes = "אחרי ארוחת בוקר")
        val items = buildAlarmDisplayItems(withNotes, category)
        assertEquals("אחרי ארוחת בוקר", items.notes)
    }
}
