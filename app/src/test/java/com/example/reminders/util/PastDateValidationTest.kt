package com.example.reminders.util

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

/**
 * Regression tests for defect 7: app allowed saving a reminder with a past date/time.
 *
 * Fix requirements:
 *   1. DateTimeUtil.isInPast() correctly identifies past vs future date+time strings.
 *   2. DateTimeUtil.nextRoundHour() always returns the next whole hour after the given instant.
 */
class PastDateValidationTest {

    // ── isInPast ──────────────────────────────────────────────────────────────

    @Test
    fun `isInPast returns true for a date clearly in the past`() {
        // FAILS before fix: function doesn't exist
        assertTrue(DateTimeUtil.isInPast("2000-01-01", "09:00"))
    }

    @Test
    fun `isInPast returns false for a date clearly in the future`() {
        // FAILS before fix: function doesn't exist
        assertFalse(DateTimeUtil.isInPast("2099-12-31", "23:59"))
    }

    // ── nextRoundHour ─────────────────────────────────────────────────────────

    @Test
    fun `nextRoundHour returns the next whole hour when mid-hour`() {
        val after = LocalDateTime.of(2024, 6, 15, 14, 35, 0)
        val result = DateTimeUtil.nextRoundHour(after)
        // FAILS before fix: function doesn't exist
        assertEquals(15, result.hour)
        assertEquals(0, result.minute)
        assertEquals(0, result.second)
    }

    @Test
    fun `nextRoundHour advances to next day when at 23xx`() {
        val after = LocalDateTime.of(2024, 6, 15, 23, 45, 0)
        val result = DateTimeUtil.nextRoundHour(after)
        // FAILS before fix: function doesn't exist
        assertEquals(2024, result.year)
        assertEquals(6, result.monthValue)
        assertEquals(16, result.dayOfMonth)
        assertEquals(0, result.hour)
        assertEquals(0, result.minute)
    }

    @Test
    fun `nextRoundHour when already on a whole hour advances to next hour`() {
        val after = LocalDateTime.of(2024, 6, 15, 14, 0, 0)
        val result = DateTimeUtil.nextRoundHour(after)
        // FAILS before fix: function doesn't exist
        assertEquals(15, result.hour)
        assertEquals(0, result.minute)
    }

    @Test
    fun `nextRoundHour result is always strictly after the input`() {
        val after = LocalDateTime.of(2024, 6, 15, 9, 59, 59)
        val result = DateTimeUtil.nextRoundHour(after)
        // FAILS before fix: function doesn't exist
        assertTrue(result.isAfter(after))
    }
}
