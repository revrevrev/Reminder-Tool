package com.example.reminders.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regression test for defect 21: editing a reminder name is restricted to a single line
 * and clipboard options (select all, copy) are inaccessible.
 * Fix: the name OutlinedTextField must NOT use singleLine = true so that long names wrap
 * and the system text-selection toolbar is available.
 */
class ReminderNameFieldMultilineTest {

    @Test
    fun `reminder name field must not be single-line`() {
        val sheetFile = File(
            "../app/src/main/java/com/example/reminders/ui/sheet/ReminderSheet.kt"
        )
        assertTrue("ReminderSheet.kt not found", sheetFile.exists())
        val source = sheetFile.readText()
        assertFalse(
            "Defect 21: name TextField must not use singleLine = true — it prevents " +
            "multi-line display and hides clipboard options (select all / copy)",
            source.contains("singleLine = true")
        )
    }
}
