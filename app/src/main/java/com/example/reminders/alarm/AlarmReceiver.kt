package com.example.reminders.alarm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.reminders.AlarmActivity
import com.example.reminders.util.NotificationUtil

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_REMINDER_ID = "reminder_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, -1)
        if (reminderId == -1) return

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminderId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPi = PendingIntent.getActivity(
            context, reminderId, alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(context, NotificationUtil.CHANNEL_ALARM)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("תזכורת")
            .setContentText("יש לך תזכורת")
            .setFullScreenIntent(fullScreenPi, true)
            .setCategory(Notification.CATEGORY_ALARM)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(reminderId, notification)

        context.startActivity(alarmIntent)
    }
}
