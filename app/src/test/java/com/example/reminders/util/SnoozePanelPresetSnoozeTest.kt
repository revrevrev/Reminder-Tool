package com.example.reminders.util

import com.example.reminders.ui.component.applySnoozePreset
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression test for defect f: tapping a snooze preset (5 / 10 / 15 / 30 min)
 * must immediately trigger the snooze action — not just update the minute display.
 *
 * Before fix: SnoozePanel presets only call onSnoozeChange; snooze requires a
 *             separate button press. applySnoozePreset does not exist → compile failure.
 * After fix:  applySnoozePreset exists and calls both callbacks, so pressing a preset
 *             triggers snooze without any extra tap.
 */
class SnoozePanelPresetSnoozeTest {

    @Test
    fun `preset tap immediately fires snooze with the selected duration`() {
        var snoozeNowCalledWith: Int? = null

        applySnoozePreset(
            preset = 5,
            onSnoozeChange = {},
            onSnoozeNow = { minutes -> snoozeNowCalledWith = minutes }
        )

        assertEquals(
            "Tapping preset 5 must trigger snooze for exactly 5 minutes without a separate button press",
            5,
            snoozeNowCalledWith
        )
    }

    @Test
    fun `preset tap also keeps the display in sync`() {
        var displayMinutes: Int? = null

        applySnoozePreset(
            preset = 10,
            onSnoozeChange = { displayMinutes = it },
            onSnoozeNow = {}
        )

        assertEquals("Display must reflect the tapped preset", 10, displayMinutes)
    }

    @Test
    fun `preset tap with null onSnoozeNow does not crash`() {
        var displayMinutes: Int? = null

        applySnoozePreset(
            preset = 15,
            onSnoozeChange = { displayMinutes = it },
            onSnoozeNow = null
        )

        assertEquals(15, displayMinutes)
    }
}
