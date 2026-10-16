package com.example.reminders.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.reminders.data.repository.ReminderRepository
import com.example.reminders.data.repository.SettingsRepository
import com.example.reminders.util.NotificationUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SnoozeReceiver : BroadcastReceiver() {

    @Inject lateinit var reminderRepo: ReminderRepository
    @Inject lateinit var settingsRepo: SettingsRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != NotificationUtil.ACTION_SNOOZE) return
        val reminderId = intent.getIntExtra(AlarmReceiver.EXTRA_REMINDER_ID, -1)
        if (reminderId == -1) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminder = reminderRepo.getById(reminderId) ?: return@launch
                val snoozeMinutes = settingsRepo.defaultSnoozeMinutes.first()
                val updated = reminderRepo.snooze(reminder, snoozeMinutes)
                alarmScheduler.schedule(updated)
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(reminderId)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
