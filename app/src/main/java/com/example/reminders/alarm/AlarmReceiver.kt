package com.example.reminders.alarm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import com.example.reminders.AlarmActivity
import com.example.reminders.data.repository.CategoryRepository
import com.example.reminders.data.repository.ReminderRepository
import com.example.reminders.util.NotificationUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var reminderRepo: ReminderRepository
    @Inject lateinit var categoryRepo: CategoryRepository

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

        val markCompletePi = PendingIntent.getBroadcast(
            context,
            reminderId + 10000,
            Intent(NotificationUtil.ACTION_MARK_COMPLETE, null, context, MarkCompleteReceiver::class.java).apply {
                putExtra(EXTRA_REMINDER_ID, reminderId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozePi = PendingIntent.getBroadcast(
            context,
            reminderId + 20000,
            Intent(NotificationUtil.ACTION_SNOOZE, null, context, SnoozeReceiver::class.java).apply {
                putExtra(EXTRA_REMINDER_ID, reminderId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminder = reminderRepo.getById(reminderId)
                val category = reminder?.let { categoryRepo.getById(it.categoryId) }

                val (title, body) = if (reminder != null && category != null)
                    NotificationUtil.buildNotificationContent(reminder, category)
                else
                    Pair("תזכורת", "יש לך תזכורת")

                val markDoneAction = Notification.Action.Builder(
                    Icon.createWithResource(context, android.R.drawable.checkbox_on_background),
                    "סיום",
                    markCompletePi
                ).build()

                val snoozeAction = Notification.Action.Builder(
                    Icon.createWithResource(context, android.R.drawable.ic_popup_reminder),
                    "נודניק",
                    snoozePi
                ).build()

                val notification = Notification.Builder(context, NotificationUtil.CHANNEL_ALARM)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setFullScreenIntent(fullScreenPi, true)
                    .setCategory(Notification.CATEGORY_ALARM)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .addAction(snoozeAction)
                    .addAction(markDoneAction)
                    .build()

                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(reminderId, notification)

                context.startActivity(alarmIntent)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
