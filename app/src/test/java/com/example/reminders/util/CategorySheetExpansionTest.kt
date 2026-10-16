package com.example.reminders.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regression test for defect 9: category sheet opens half-expanded, requiring scroll to see content.
 * Fix: ModalBottomSheet must have skipPartiallyExpanded = true.
 */
class CategorySheetExpansionTest {

    @Test
    fun `CategorySheet skips partially expanded state`() {
        val sheetFile = File(
            "../app/src/main/java/com/example/reminders/ui/sheet/CategorySheet.kt"
        )
        assertTrue(
            "CategorySheet.kt not found at expected path",
            sheetFile.exists()
        )
        val source = sheetFile.readText()
        assertTrue(
            "ModalBottomSheet in CategorySheet must use skipPartiallyExpanded = true " +
            "(defect 9: sheet opens half-expanded, forcing the user to scroll)",
            source.contains("skipPartiallyExpanded = true")
        )
    }
}
