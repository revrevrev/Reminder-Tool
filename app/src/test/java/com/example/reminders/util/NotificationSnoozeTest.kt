package com.example.reminders.util

import com.example.reminders.alarm.AlarmReceiver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regression test for defect 11: notification must expose a "snooze" action button.
 *
 * Before fix: ACTION_SNOOZE and snoozeExtras do not exist on NotificationUtil.
 * After fix: both compile and assert correctly.
 */
class NotificationSnoozeTest {

    @Test
    fun `ACTION_SNOOZE constant is defined and non-empty`() {
        assertTrue(
            "ACTION_SNOOZE must be a non-empty action string",
            NotificationUtil.ACTION_SNOOZE.isNotEmpty()
        )
    }

    @Test
    fun `snoozeExtras returns the supplied reminder id under the correct key`() {
        val reminderId = 42
        val extras = NotificationUtil.snoozeExtras(reminderId)
        assertEquals(
            "Extras map must contain the reminder id under EXTRA_REMINDER_ID key",
            reminderId,
            extras[AlarmReceiver.EXTRA_REMINDER_ID]
        )
    }
}
