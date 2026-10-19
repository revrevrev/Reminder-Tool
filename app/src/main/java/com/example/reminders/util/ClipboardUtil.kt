package com.example.reminders.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun buildClipboardText(reminderName: String, categoryName: String?): String =
    if (categoryName.isNullOrEmpty()) reminderName else "$reminderName - $categoryName"

fun copyReminderToClipboard(context: Context, reminderName: String, categoryName: String?) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("reminder", buildClipboardText(reminderName, categoryName)))
}
