package com.example.reminders.util

import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression test for defect 4: notification must show the reminder's name and category name
 * instead of the hardcoded Hebrew strings "תזכורת" / "יש לך תזכורת".
 */
class NotificationContentTest {

    private val category = Category(id = 1, name = "עבודה", color = "accent-500", sound = "chime", volume = 80)
    private val reminder = Reminder(id = 42, name = "פגישת צוות", type = ReminderType.ONCE, time = "09:00", categoryId = 1)

    @Test
    fun `notification title is reminder name`() {
        val content = NotificationUtil.buildNotificationContent(reminder, category)
        assertEquals("Notification title must be the reminder name", reminder.name, content.first)
    }

    @Test
    fun `notification body is category name`() {
        val content = NotificationUtil.buildNotificationContent(reminder, category)
        assertEquals("Notification body must be the category name", category.name, content.second)
    }
}
