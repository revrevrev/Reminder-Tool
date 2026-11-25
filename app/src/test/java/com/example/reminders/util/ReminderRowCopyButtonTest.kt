package com.example.reminders.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regression test for defect 23: each reminder in the list must have a copy button that
 * copies the same content as dismissing (reminder name - category) to the clipboard.
 *
 * Before fix: ReminderRow has no onCopy callback / copy button and HomeScreen never
 * copies a reminder to the clipboard from the list.
 */
class ReminderRowCopyButtonTest {

    @Test
    fun `ReminderRow exposes an onCopy callback and a copy button`() {
        val rowFile = File(
            "../app/src/main/java/com/example/reminders/ui/component/ReminderRow.kt"
        )
        assertTrue("ReminderRow.kt not found", rowFile.exists())
        val source = rowFile.readText()
        assertTrue(
            "Defect 23: ReminderRow must expose an onCopy callback for the copy button",
            source.contains("onCopy")
        )
        assertTrue(
            "Defect 23: ReminderRow must render a copy icon button (ContentCopy)",
            source.contains("ContentCopy")
        )
    }

    @Test
    fun `HomeScreen copies the reminder to clipboard from the list`() {
        val homeFile = File(
            "../app/src/main/java/com/example/reminders/ui/screen/HomeScreen.kt"
        )
        assertTrue("HomeScreen.kt not found", homeFile.exists())
        val source = homeFile.readText()
        assertTrue(
            "Defect 23: HomeScreen must copy the reminder to the clipboard when the " +
            "list copy button is pressed",
            source.contains("copyReminderToClipboard") && source.contains("onCopy")
        )
    }
}
