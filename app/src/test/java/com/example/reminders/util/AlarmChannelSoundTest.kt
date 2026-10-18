package com.example.reminders.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import org.junit.Test
import org.mockito.Mockito.mockConstruction
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.*

/**
 * Regression test for defect 14: when an alarm fires, two sounds play simultaneously.
 *
 * Root cause: the alarm notification channel was created with setSound(alarmUri, audioAttr).
 * When the notification is posted, Android plays the channel's sound (default alarm ringtone)
 * while AlarmActivity simultaneously calls SoundPlayer.startAlarm() for the custom sound.
 *
 * Fix: create the alarm channel with setSound(null, null) so sound is managed exclusively
 * by SoundPlayer in AlarmActivity.
 */
class AlarmChannelSoundTest {

    @Test
    fun `alarm channel is created without sound so SoundPlayer is the sole sound source`() {
        val mockNm: NotificationManager = mock()
        val mockContext: Context = mock()
        val mockUri: Uri = mock()
        whenever(mockContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNm)

        // Return a real (non-null) mock URI so the pre-fix code passes a non-null sound to
        // setSound(), causing this test to FAIL before the fix is applied.
        mockStatic(RingtoneManager::class.java).use { staticMock ->
            staticMock.`when`<Uri> { RingtoneManager.getDefaultUri(any()) }.thenReturn(mockUri)

            mockConstruction(AudioAttributes.Builder::class.java) { builder, _ ->
                val mockAttrs: AudioAttributes = mock()
                whenever(builder.setUsage(any<Int>())).thenReturn(builder)
                whenever(builder.setContentType(any<Int>())).thenReturn(builder)
                whenever(builder.build()).thenReturn(mockAttrs)
            }.use {
                mockConstruction(NotificationChannel::class.java).use { channelConstruction ->

                    NotificationUtil.createChannels(mockContext)

                    val channel = channelConstruction.constructed().first()
                    // FAILS before fix: setSound(mockUri, mockAttrs) was called with non-null args.
                    // PASSES after fix: setSound(null, null) is called, channel plays no sound.
                    verify(channel).setSound(null as Uri?, null as AudioAttributes?)
                }
            }
        }
    }
}
