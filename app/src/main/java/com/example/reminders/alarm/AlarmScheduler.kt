package com.example.reminders.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import com.example.reminders.util.DateTimeUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(@ApplicationContext private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(reminder: Reminder) {
        if (reminder.paused || reminder.archived) {
            cancel(reminder.id)
            return
        }
        val fireTimeMs = DateTimeUtil.nextFireTimeMs(reminder)
        if (fireTimeMs <= System.currentTimeMillis() && reminder.type == ReminderType.ONCE) {
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminder.id)
        }
        val pi = PendingIntent.getBroadcast(
            context, reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return
        }

        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(fireTimeMs, pi), pi)
    }

    fun cancel(reminderId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, reminderId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        alarmManager.cancel(pi)
        pi.cancel()
    }

    fun scheduleAll(reminders: List<Reminder>) {
        reminders.filter { !it.paused && !it.archived }.forEach { schedule(it) }
    }
}
