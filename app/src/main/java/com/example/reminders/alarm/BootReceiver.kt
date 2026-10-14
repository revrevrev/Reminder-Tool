package com.example.reminders.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.reminders.data.repository.ReminderRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var reminderRepository: ReminderRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.QUICKBOOT_POWERON") return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminders = reminderRepository.getActiveNonPaused()
                alarmScheduler.scheduleAll(reminders)
            } finally {
                pending.finish()
            }
        }
    }
}
