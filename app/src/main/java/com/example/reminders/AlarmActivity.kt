package com.example.reminders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.example.reminders.alarm.AlarmReceiver
import com.example.reminders.ui.alarm.AlarmScreen
import com.example.reminders.ui.theme.RemindersTheme
import com.example.reminders.util.SoundPlayer
import com.example.reminders.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    private val vm: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val reminderId = intent.getIntExtra(AlarmReceiver.EXTRA_REMINDER_ID, -1)
        if (reminderId == -1) { finish(); return }

        vm.loadReminder(reminderId)

        // Start alarm sound
        val effectiveSound = vm.effectiveSound()
        val effectiveVolume = vm.effectiveVolume()
        val effectiveContinuous = vm.effectiveContinuous()
        SoundPlayer.startAlarm(this, effectiveSound, effectiveVolume, effectiveContinuous)

        setContent {
            RemindersTheme {
                AlarmScreen(
                    vm = vm,
                    onSnooze = {
                        SoundPlayer.stop()
                        finish()
                    },
                    onDone = {
                        SoundPlayer.stop()
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
}
