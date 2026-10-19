package com.example.reminders.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regression test for defect 18: scrolling inside the new-reminder sheet dismisses it.
 * Fix: ModalBottomSheet in ReminderSheet must set confirmValueChange to block SheetValue.Hidden,
 * so that a swipe/drag gesture cannot dismiss the sheet.
 */
class ReminderSheetSwipeDismissTest {

    @Test
    fun `ReminderSheet prevents swipe-to-dismiss via confirmValueChange`() {
        val sheetFile = File(
            "../app/src/main/java/com/example/reminders/ui/sheet/ReminderSheet.kt"
        )
        assertTrue("ReminderSheet.kt not found", sheetFile.exists())
        val source = sheetFile.readText()
        assertTrue(
            "ModalBottomSheet in ReminderSheet must set confirmValueChange to block SheetValue.Hidden " +
            "(defect 18: scroll gesture dismisses the dialog while the user is filling it in)",
            source.contains("confirmValueChange") && source.contains("SheetValue.Hidden")
        )
    }
}
