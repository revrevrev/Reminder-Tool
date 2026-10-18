package com.example.reminders.util

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.Resources
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.example.reminders.data.model.Category
import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mockConstruction
import org.mockito.kotlin.*

/**
 * Regression test for defect 16: when the screen is not locked, Android shows the alarm as a
 * heads-up notification instead of launching AlarmActivity via the full-screen intent.
 * AlarmActivity was the sole caller of SoundPlayer.startAlarm(), so no sound played on an
 * unlocked screen.
 *
 * Fix: sound is now started from AlarmReceiver via NotificationUtil.dispatchAlarmSound(),
 * ensuring it plays whether or not AlarmActivity is ever launched.
 */
class UnlockedScreenSoundTest {

    private lateinit var mockMp: MediaPlayer
    private lateinit var mockContext: Context
    private lateinit var mockResources: Resources
    private lateinit var mockAfd: AssetFileDescriptor

    @Before
    fun setUp() {
        mockMp = mock()
        mockContext = mock()
        mockResources = mock()
        mockAfd = mock()

        val mockAm: AudioManager = mock()
        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAm)
        whenever(mockResources.openRawResourceFd(any<Int>())).thenReturn(mockAfd)

        SoundPlayer.playerFactory = { mockMp }
    }

    @After
    fun tearDown() {
        SoundPlayer.playerFactory = { MediaPlayer() }
        SoundPlayer.stop()
    }

    /**
     * Verifies that dispatchAlarmSound() — the entry point called from AlarmReceiver —
     * starts the MediaPlayer. Before the fix this method did not exist, so no sound played
     * when AlarmActivity was not launched (unlocked screen).
     *
     * FAILS before fix: NotificationUtil.dispatchAlarmSound() does not exist → compile error
     * PASSES after fix: method exists and delegates to SoundPlayer.startAlarm() → mp.start()
     */
    @Test
    fun `alarm sound starts when notification is dispatched even without AlarmActivity`() {
        val reminder = Reminder(
            id = 1, name = "Test", categoryId = 1,
            type = ReminderType.ONCE, time = "09:00",
            sound = "bell", volume = 80, continuous = false
        )
        val category = Category(id = 1, name = "Cat", color = "accent-500",
            sound = "chime", volume = 70, continuous = false)

        mockConstruction(AudioAttributes.Builder::class.java) { builder, _ ->
            whenever(builder.setUsage(any<Int>())).thenReturn(builder)
            whenever(builder.setContentType(any<Int>())).thenReturn(builder)
            doReturn(null).whenever(builder).build()
        }.use {
            NotificationUtil.dispatchAlarmSound(mockContext, reminder, category)
            verify(mockMp).start()
        }
    }
}
