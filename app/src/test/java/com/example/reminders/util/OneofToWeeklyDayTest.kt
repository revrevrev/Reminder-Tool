package com.example.reminders.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for DateTimeUtil.dateStringToDayIndex, which pre-selects the weekday picker
 * when the user switches a one-time reminder to weekly recurrence.
 * Index convention: 0=Sunday, 1=Monday, …, 6=Saturday.
 */
class OneofToWeeklyDayTest {

    @Test
    fun `Sunday date returns index 0`() {
        assertEquals(0, DateTimeUtil.dateStringToDayIndex("2024-06-16")) // Sun
    }

    @Test
    fun `Monday date returns index 1`() {
        assertEquals(1, DateTimeUtil.dateStringToDayIndex("2024-06-10")) // Mon
    }

    @Test
    fun `Tuesday date returns index 2`() {
        assertEquals(2, DateTimeUtil.dateStringToDayIndex("2024-06-11")) // Tue
    }

    @Test
    fun `Wednesday date returns index 3`() {
        assertEquals(3, DateTimeUtil.dateStringToDayIndex("2024-06-12")) // Wed
    }

    @Test
    fun `Thursday date returns index 4`() {
        assertEquals(4, DateTimeUtil.dateStringToDayIndex("2024-06-13")) // Thu
    }

    @Test
    fun `Friday date returns index 5`() {
        assertEquals(5, DateTimeUtil.dateStringToDayIndex("2024-06-14")) // Fri
    }

    @Test
    fun `Saturday date returns index 6`() {
        assertEquals(6, DateTimeUtil.dateStringToDayIndex("2024-06-15")) // Sat
    }

    @Test
    fun `all indices are in 0-6 range`() {
        val dates = listOf(
            "2024-06-10", "2024-06-11", "2024-06-12", "2024-06-13",
            "2024-06-14", "2024-06-15", "2024-06-16"
        )
        for (date in dates) {
            val idx = DateTimeUtil.dateStringToDayIndex(date)
            assert(idx in 0..6) { "Index $idx out of range for date $date" }
        }
    }

    @Test
    fun `index matches LocalDate dayOfWeek for spot-check dates`() {
        val dates = mapOf(
            "2024-06-10" to 1, // Mon
            "2024-06-11" to 2, // Tue
            "2024-06-15" to 6, // Sat
            "2024-06-16" to 0, // Sun
        )
        for ((date, expected) in dates) {
            assertEquals("Mismatch for $date", expected, DateTimeUtil.dateStringToDayIndex(date))
        }
    }
}
