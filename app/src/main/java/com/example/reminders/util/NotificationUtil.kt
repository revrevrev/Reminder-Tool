package com.example.reminders.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder

object NotificationUtil {
    const val CHANNEL_ALARM = "reminders_alarm"
    const val ACTION_MARK_COMPLETE = "com.example.reminders.ACTION_MARK_COMPLETE"
    const val ACTION_SNOOZE = "com.example.reminders.ACTION_SNOOZE"

    fun markCompleteExtras(reminderId: Int): Map<String, Int> =
        mapOf("reminder_id" to reminderId)

    fun snoozeExtras(reminderId: Int): Map<String, Int> =
        mapOf("reminder_id" to reminderId)

    fun buildNotificationContent(reminder: Reminder, category: Category): Pair<String, String> =
        Pair(reminder.name, category.name)

    fun resolveAlarmSound(reminder: Reminder?, category: Category?): String =
        reminder?.sound ?: category?.sound ?: "chime"

    fun resolveAlarmVolume(reminder: Reminder?, category: Category?): Int =
        reminder?.volume ?: category?.volume ?: 70

    fun resolveAlarmContinuous(reminder: Reminder?, category: Category?): Boolean =
        reminder?.continuous ?: category?.continuous ?: false

    fun createChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // If the channel was previously created with a sound, delete it so the new channel
        // (without sound) takes effect. Android ignores sound changes on existing channels.
        if (nm.getNotificationChannel(CHANNEL_ALARM)?.sound != null) {
            nm.deleteNotificationChannel(CHANNEL_ALARM)
        }
        val channel = NotificationChannel(
            CHANNEL_ALARM, "תזכורות", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "התראות תזכורות"
            // No channel sound — AlarmActivity plays sound exclusively via SoundPlayer.
            setSound(null, null)
            enableVibration(true)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }
        nm.createNotificationChannel(channel)
    }
}
