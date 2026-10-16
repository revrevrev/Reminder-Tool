package com.example.reminders

import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.reminders.alarm.AlarmReceiver
import com.example.reminders.ui.alarm.AlarmScreen
import com.example.reminders.ui.theme.RemindersTheme
import com.example.reminders.util.NotificationUtil
import com.example.reminders.util.SoundPlayer
import com.example.reminders.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    private val vm: AlarmViewModel by viewModels()
    private var reminderId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        reminderId = intent.getIntExtra(AlarmReceiver.EXTRA_REMINDER_ID, -1)
        if (reminderId == -1) { finish(); return }

        vm.loadReminder(reminderId)

        // Start alarm sound only after the ViewModel state has loaded from the DB.
        // Without this, effectiveSound() would always return "chime" because it reads from
        // the StateFlow which is still empty when called synchronously after loadReminder().
        lifecycleScope.launch {
            val state = vm.uiState.first { it.reminder != null }
            SoundPlayer.startAlarm(
                this@AlarmActivity,
                NotificationUtil.resolveAlarmSound(state.reminder, state.category),
                NotificationUtil.resolveAlarmVolume(state.reminder, state.category),
                NotificationUtil.resolveAlarmContinuous(state.reminder, state.category)
            )
        }

        setContent {
            RemindersTheme {
                AlarmScreen(
                    vm = vm,
                    onSnooze = {
                        SoundPlayer.stop()
                        cancelNotification()
                        finish()
                    },
                    onDone = {
                        SoundPlayer.stop()
                        cancelNotification()
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundPlayer.stop()
    }

    private fun cancelNotification() {
        if (reminderId != -1) {
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(reminderId)
        }
    }
}
