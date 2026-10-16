package com.example.reminders.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder

object NotificationUtil {
    const val CHANNEL_ALARM = "reminders_alarm"

    fun buildNotificationContent(reminder: Reminder, category: Category): Pair<String, String> =
        Pair(reminder.name, category.name)

    fun createChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val audioAttr = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val channel = NotificationChannel(
            CHANNEL_ALARM, "תזכורות", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "התראות תזכורות"
            setSound(alarmUri, audioAttr)
            enableVibration(true)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }
        nm.createNotificationChannel(channel)
    }
}
