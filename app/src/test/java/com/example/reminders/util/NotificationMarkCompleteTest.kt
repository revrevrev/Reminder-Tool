package com.example.reminders.util

import com.example.reminders.alarm.AlarmReceiver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regression test for defect 5: notification must expose a "mark complete" action button.
 *
 * Before fix: NotificationUtil.ACTION_MARK_COMPLETE does not exist, and there is no helper
 * that bundles the reminder-id extra for the mark-complete broadcast intent.
 *
 * After fix: both compile and assert correctly.
 */
class NotificationMarkCompleteTest {

    @Test
    fun `ACTION_MARK_COMPLETE constant is defined and non-empty`() {
        // Fails before fix: field does not exist on NotificationUtil
        assertTrue(
            "ACTION_MARK_COMPLETE must be a non-empty action string",
            NotificationUtil.ACTION_MARK_COMPLETE.isNotEmpty()
        )
    }

    @Test
    fun `markCompleteExtras returns the supplied reminder id under the correct key`() {
        val reminderId = 77
        // Fails before fix: markCompleteExtras does not exist
        val extras = NotificationUtil.markCompleteExtras(reminderId)
        assertEquals(
            "Extras map must contain the reminder id under EXTRA_REMINDER_ID key",
            reminderId,
            extras[AlarmReceiver.EXTRA_REMINDER_ID]
        )
    }
}
