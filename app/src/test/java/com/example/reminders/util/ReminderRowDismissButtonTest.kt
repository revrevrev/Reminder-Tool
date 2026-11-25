package com.example.reminders.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regression test for defect 24: each reminder in the list must have a dismiss button.
 *
 * Behaviour (Option A, confirmed with the user):
 *  - Pressing dismiss cancels the alert but the item REMAINS in the list (it is not
 *    removed like "done" is). The row shows a dismissed state and the SAME button
 *    switches to an "undo" icon so the user can un-dismiss it.
 *  - Undo is available only "as long as still relevant" — i.e. until the reminder's
 *    scheduled time arrives. When its time comes, the dismissed reminder is finalized
 *    silently (ONCE archives, WEEKLY advances to next week) instead of ringing.
 *
 * Before fix: there is no `dismissed` field, no dismiss button / onDismiss callback,
 * HomeScreen never toggles dismissal, and AlarmReceiver always rings regardless of
 * whether the reminder was dismissed in advance.
 */
class ReminderRowDismissButtonTest {

    private fun source(path: String): String {
        val file = File(path)
        assertTrue("$path not found", file.exists())
        return file.readText()
    }

    @Test
    fun `Reminder model has a dismissed flag`() {
        val src = source("../app/src/main/java/com/example/reminders/data/model/Reminder.kt")
        assertTrue(
            "Defect 24: Reminder must carry a `dismissed` flag so an item can stay in " +
            "the list in a dismissed state",
            src.contains("dismissed")
        )
    }

    @Test
    fun `ReminderRow exposes an onDismiss callback and a toggling dismiss or undo icon`() {
        val src = source("../app/src/main/java/com/example/reminders/ui/component/ReminderRow.kt")
        assertTrue(
            "Defect 24: ReminderRow must expose an onDismiss callback for the dismiss button",
            src.contains("onDismiss")
        )
        assertTrue(
            "Defect 24: the dismiss button must reflect the reminder's dismissed state",
            src.contains("reminder.dismissed")
        )
        assertTrue(
            "Defect 24: the button must show a dismiss icon when active and an undo icon " +
            "when already dismissed",
            src.contains("NotificationsOff") && src.contains("Replay")
        )
    }

    @Test
    fun `HomeScreen toggles dismissal from the list`() {
        val src = source("../app/src/main/java/com/example/reminders/ui/screen/HomeScreen.kt")
        assertTrue(
            "Defect 24: HomeScreen must wire the row's onDismiss to a toggleDismiss action",
            src.contains("onDismiss") && src.contains("toggleDismiss")
        )
    }

    @Test
    fun `HomeViewModel implements toggleDismiss keeping the item in the list`() {
        val src = source("../app/src/main/java/com/example/reminders/viewmodel/HomeViewModel.kt")
        assertTrue(
            "Defect 24: HomeViewModel must implement toggleDismiss",
            src.contains("fun toggleDismiss")
        )
        assertTrue(
            "Defect 24: toggleDismiss must set the dismissed flag (not archive/delete the item)",
            src.contains("setDismissed")
        )
    }

    @Test
    fun `AlarmReceiver finalizes a dismissed reminder silently instead of ringing`() {
        val src = source("../app/src/main/java/com/example/reminders/alarm/AlarmReceiver.kt")
        assertTrue(
            "Defect 24: when a dismissed reminder's time arrives, AlarmReceiver must finalize " +
            "it (markDone) instead of showing the alarm",
            src.contains("dismissed") && src.contains("markDone")
        )
    }
}
