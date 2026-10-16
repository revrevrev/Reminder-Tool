package com.example.reminders.util

import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Regression tests for defect 8: dismissing a weekly reminder before its alarm fires
 * kept it as "up next" because skipUntil was set to last week's occurrence instead
 * of the current week's.
 *
 * Root cause: currentOccurrenceMs() used latestWeeklyOccurrenceAtOrBefore(), which
 * returns last week when now < this week's fire time.
 *
 * Fix requirement: currentOccurrenceMs() must accept a nowMs parameter and return
 * the occurrence NEAREST to now (not just the most recent past one).
 */
class WeeklyDismissTest {

    // 2024-10-02 is a Wednesday
    private val thisWed900 = LocalDateTime.of(2024, 10, 2, 9, 0, 0)
    private val thisWed855 = LocalDateTime.of(2024, 10, 2, 8, 55, 0)  // 5 min before
    private val thisWed905 = LocalDateTime.of(2024, 10, 2, 9, 5, 0)   // 5 min after
    private val nextWed900 = thisWed900.plusWeeks(1)

    private fun ms(dt: LocalDateTime) =
        dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private val reminder = Reminder(
        id = 1, name = "Test", type = ReminderType.WEEKLY,
        day = 3,          // 3 = Wednesday (0=Sun … 6=Sat)
        time = "09:00", categoryId = 1
    )

    @Test
    fun `marking done before alarm fires skips current week not last week`() {
        // Simulate what markDone does: compute skipUntil via currentOccurrenceMs.
        // FAILS before fix: currentOccurrenceMs does not accept a nowMs parameter.
        val occurrence = DateTimeUtil.currentOccurrenceMs(reminder, ms(thisWed855))
        val updated = reminder.copy(skipUntil = occurrence, snoozeUntil = null)

        val nextFire = DateTimeUtil.nextWeeklyOccurrenceMs(updated, after = ms(thisWed855))

        assertEquals(
            "After marking done 5 min before alarm, next fire must be NEXT week",
            ms(nextWed900),
            nextFire
        )
    }

    @Test
    fun `marking done after alarm fires still skips to next week`() {
        // Existing correct path: now is 5 min after the alarm.
        val occurrence = DateTimeUtil.currentOccurrenceMs(reminder, ms(thisWed905))
        val updated = reminder.copy(skipUntil = occurrence, snoozeUntil = null)

        val nextFire = DateTimeUtil.nextWeeklyOccurrenceMs(updated, after = ms(thisWed905))

        assertEquals(
            "After marking done 5 min after alarm, next fire must be NEXT week",
            ms(nextWed900),
            nextFire
        )
    }

    @Test
    fun `marking done from home screen day before alarm fires skips current week`() {
        // now = Tuesday 10:00 AM, alarm is tomorrow Wednesday 9:00 AM
        val tuesdayAt10 = LocalDateTime.of(2024, 10, 1, 10, 0, 0)

        val occurrence = DateTimeUtil.currentOccurrenceMs(reminder, ms(tuesdayAt10))
        val updated = reminder.copy(skipUntil = occurrence, snoozeUntil = null)

        val nextFire = DateTimeUtil.nextWeeklyOccurrenceMs(updated, after = ms(tuesdayAt10))

        assertEquals(
            "Marked done on Tuesday should push to the Wednesday AFTER the upcoming one",
            ms(nextWed900),
            nextFire
        )
    }
}
