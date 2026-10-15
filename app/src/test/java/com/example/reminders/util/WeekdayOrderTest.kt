package com.example.reminders.util

import org.junit.Assert.assertEquals
import org.junit.Test

class WeekdayOrderTest {

    /**
     * Regression test for: WeekdayPicker showed Saturday (ש׳) on the right in RTL layout.
     * Hebrew week starts on Sunday (א׳), so Sunday must appear rightmost in RTL.
     *
     * In RTL layout the first list element renders on the right.
     * So weekdayPickerOrder()[0] must be Sunday (day index 0).
     */
    @Test
    fun `weekday picker RTL order starts with Sunday on the right`() {
        val order = DateTimeUtil.weekdayPickerOrder()
        // FAILS before fix: returns Sat (index 6) first — PASSES after fix: returns Sun (index 0) first
        assertEquals("First RTL position (rightmost) should be Sunday (day 0)", 0, order[0].second)
    }

    @Test
    fun `weekday picker RTL order ends with Saturday on the left`() {
        val order = DateTimeUtil.weekdayPickerOrder()
        assertEquals("Last RTL position (leftmost) should be Saturday (day 6)", 6, order.last().second)
    }

    @Test
    fun `weekday picker shows all 7 days`() {
        val order = DateTimeUtil.weekdayPickerOrder()
        assertEquals(7, order.size)
    }
}
