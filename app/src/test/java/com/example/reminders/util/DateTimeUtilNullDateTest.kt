package com.example.reminders.util

import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression tests for defect 15: nextFireTimeMs() and currentOccurrenceMs() used !! on
 * reminder.date for ONCE-type reminders. A ONCE reminder with a null date (e.g. due to a
 * database migration issue or data corruption) would throw KotlinNullPointerException, crashing
 * the HomeViewModel flow that calls nextFireTimeMs() for every reminder in the list.
 *
 * Fix: replace !! with a safe fallback — Long.MAX_VALUE for nextFireTimeMs (sort to end of list),
 * 0L for currentOccurrenceMs (epoch zero is a safe no-op for mark-done).
 */
class DateTimeUtilNullDateTest {

    private val onceMissingDate = Reminder(
        id = 1, name = "corrupted", type = ReminderType.ONCE,
        date = null, time = "09:00", categoryId = 1
    )

    @Test
    fun `nextFireTimeMs does not throw for ONCE reminder with null date`() {
        // FAILS before fix: KotlinNullPointerException from reminder.date!!
        // PASSES after fix: returns Long.MAX_VALUE (reminder sorts to end of list)
        val result = DateTimeUtil.nextFireTimeMs(onceMissingDate)
        assertEquals(Long.MAX_VALUE, result)
    }

    @Test
    fun `currentOccurrenceMs does not throw for ONCE reminder with null date`() {
        // FAILS before fix: KotlinNullPointerException from reminder.date!!
        // PASSES after fix: returns 0L
        val result = DateTimeUtil.currentOccurrenceMs(onceMissingDate)
        assertEquals(0L, result)
    }
}
