package com.example.reminders.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Tests for DateTimeUtil.nextSameDayOfWeekDateString, which drives the archive-restore
 * date bump: when restoring a past-dated reminder, its date is moved to the next upcoming
 * occurrence of the same day of week (never today, always tomorrow or later).
 */
class ArchiveRestoreDateTest {

    // today = Monday 2024-06-10

    @Test
    fun `restoring a Wednesday reminder on Monday returns next Wednesday`() {
        // today=Mon, tomorrow=Tue → nextOrSame(Wed) from Tue = Wed Jun 12
        val result = DateTimeUtil.nextSameDayOfWeekDateString("2024-01-03", LocalDate.of(2024, 6, 10))
        assertEquals("2024-06-12", result)
    }

    @Test
    fun `restoring a Tuesday reminder on Monday returns tomorrow`() {
        // today=Mon, tomorrow=Tue → nextOrSame(Tue) from Tue = Tue Jun 11 (tomorrow itself)
        val result = DateTimeUtil.nextSameDayOfWeekDateString("2024-01-02", LocalDate.of(2024, 6, 10))
        assertEquals("2024-06-11", result)
    }

    @Test
    fun `restoring a Monday reminder on Monday skips to next Monday`() {
        // today=Mon, tomorrow=Tue → nextOrSame(Mon) from Tue = Mon Jun 17 (next week)
        val result = DateTimeUtil.nextSameDayOfWeekDateString("2024-01-01", LocalDate.of(2024, 6, 10))
        assertEquals("2024-06-17", result)
    }

    @Test
    fun `restoring a Sunday reminder on Monday returns next Sunday`() {
        // today=Mon Jun 10, tomorrow=Tue Jun 11 → nextOrSame(Sun) from Tue = Sun Jun 16
        val result = DateTimeUtil.nextSameDayOfWeekDateString("2024-01-07", LocalDate.of(2024, 6, 10))
        assertEquals("2024-06-16", result)
    }

    @Test
    fun `restoring a Saturday reminder on Saturday skips to next Saturday`() {
        // today=Sat Jun 15, tomorrow=Sun Jun 16 → nextOrSame(Sat) from Sun = Sat Jun 22
        val result = DateTimeUtil.nextSameDayOfWeekDateString("2024-01-06", LocalDate.of(2024, 6, 15))
        assertEquals("2024-06-22", result)
    }

    @Test
    fun `restoring a Wednesday reminder on Tuesday returns tomorrow`() {
        // today=Tue Jun 11, tomorrow=Wed Jun 12 → nextOrSame(Wed) from Wed = Wed Jun 12
        val result = DateTimeUtil.nextSameDayOfWeekDateString("2024-01-03", LocalDate.of(2024, 6, 11))
        assertEquals("2024-06-12", result)
    }

    @Test
    fun `result day of week always matches original date day of week`() {
        val originalDates = listOf(
            "2024-01-01", // Mon
            "2024-01-02", // Tue
            "2024-01-03", // Wed
            "2024-01-04", // Thu
            "2024-01-05", // Fri
            "2024-01-06", // Sat
            "2024-01-07", // Sun
        )
        val today = LocalDate.of(2024, 6, 10) // Monday
        for (original in originalDates) {
            val expected = LocalDate.parse(original).dayOfWeek
            val result = LocalDate.parse(DateTimeUtil.nextSameDayOfWeekDateString(original, today))
            assertEquals("Day of week mismatch for original $original", expected, result.dayOfWeek)
        }
    }

    @Test
    fun `result is always strictly after today`() {
        val today = LocalDate.of(2024, 6, 10)
        val originals = listOf("2024-01-01", "2024-01-02", "2024-01-03", "2024-01-04",
            "2024-01-05", "2024-01-06", "2024-01-07")
        for (original in originals) {
            val result = LocalDate.parse(DateTimeUtil.nextSameDayOfWeekDateString(original, today))
            assert(result.isAfter(today)) { "Expected result $result to be after today $today for original $original" }
        }
    }

    @Test
    fun `result is at most 7 days from tomorrow`() {
        val today = LocalDate.of(2024, 6, 10)
        val tomorrow = today.plusDays(1)
        val originals = listOf("2024-01-01", "2024-01-02", "2024-01-03", "2024-01-04",
            "2024-01-05", "2024-01-06", "2024-01-07")
        for (original in originals) {
            val result = LocalDate.parse(DateTimeUtil.nextSameDayOfWeekDateString(original, today))
            assert(!result.isAfter(tomorrow.plusDays(6))) {
                "Expected result $result to be within 7 days of tomorrow for original $original"
            }
        }
    }
}
