package com.example.reminders.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression test for defect e: when a reminder is dismissed/snoozed,
 * clipboard text must be formatted as "reminder name - category".
 *
 * Before fix: buildClipboardText does not exist.
 * After fix: function exists and formats correctly in all cases.
 */
class ClipboardTextTest {

    @Test
    fun `buildClipboardText returns name dash category when category is present`() {
        assertEquals("Call dentist - Health", buildClipboardText("Call dentist", "Health"))
    }

    @Test
    fun `buildClipboardText returns name only when category is null`() {
        assertEquals("Call dentist", buildClipboardText("Call dentist", null))
    }

    @Test
    fun `buildClipboardText returns name only when category is empty`() {
        assertEquals("Call dentist", buildClipboardText("Call dentist", ""))
    }
}
