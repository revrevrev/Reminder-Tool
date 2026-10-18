package com.example.reminders.util

import com.example.reminders.data.db.ReminderDao
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import com.example.reminders.data.repository.ReminderRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Regression test for defect 17: snoozed recurring reminders fire at next week's
 * occurrence + snooze_minutes instead of now + snooze_minutes.
 *
 * Root cause: ReminderRepository.snooze() used nextFireTimeMs(reminder) as the base,
 * which for WEEKLY reminders calls nextWeeklyOccurrenceMs(after = now). When called
 * right after this week's alarm fired (now ≈ fireTime), that function returns the
 * occurrence STRICTLY AFTER now — i.e. next week.
 *
 * Fix: base the snooze time on nowMs (passed in), not on the next weekly occurrence.
 */
class RecurringSnoozeTest {

    private fun ms(dt: LocalDateTime) =
        dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    // Wednesday (day=3 in 0=Sun..6=Sat encoding) at 09:00
    private val reminder = Reminder(
        id = 1, name = "test", type = ReminderType.WEEKLY,
        day = 3, time = "09:00", categoryId = 1
    )

    @Test
    fun `snooze for weekly reminder fires at now plus snooze minutes not next week`(): Unit =
        runBlocking {
            val snoozeMinutes = 5
            // Alarm fires 2024-10-02 Wed 09:00; snooze pressed 2 seconds later
            val fireTimeMs = ms(LocalDateTime.of(2024, 10, 2, 9, 0, 0))
            val nowMs = fireTimeMs + 2_000L

            val dao = mock<ReminderDao>()
            whenever(dao.update(any())).thenReturn(Unit)
            val repo = ReminderRepository(dao)

            // snooze() must accept a nowMs param so the calculation can be tested with a
            // controlled clock. This call will not compile until the param is added.
            val result = repo.snooze(reminder, snoozeMinutes, nowMs)

            val expectedSnoozeUntil = nowMs + snoozeMinutes * 60_000L
            assertEquals(
                "snoozeUntil must be exactly $snoozeMinutes min after now, not next week",
                expectedSnoozeUntil,
                result.snoozeUntil
            )
        }
}
