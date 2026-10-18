package com.example.reminders.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.mockito.Mockito.mockConstruction
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.*

/**
 * Regression tests for defect 14: when an alarm fires, two sounds play simultaneously.
 *
 * Root cause (v1): the alarm notification channel was created with setSound(alarmUri, audioAttr),
 * so Android played the channel ringtone while AlarmActivity also called SoundPlayer.startAlarm().
 *
 * Root cause (v2): even after fixing the setSound() call, using the same channel ID
 * ("reminders_alarm") on existing devices means Android may restore the old cached settings —
 * including the original sound — when the channel is deleted and recreated with the same ID.
 * A versioned channel ID guarantees a genuinely fresh, sound-free channel on all devices.
 */
class AlarmChannelSoundTest {

    @Test
    fun `alarm channel ID is not the original sound-bearing channel that Android may restore from cache`() {
        // The original channel "reminders_alarm" was registered in the OS with a sound URI.
        // Deleting + recreating with the same ID can have the OS restore old settings on some
        // Android versions / OEMs, bringing back the sound. A new ID guarantees a fresh channel.
        // FAILS before fix: CHANNEL_ALARM == "reminders_alarm"
        // PASSES after fix: CHANNEL_ALARM is a new versioned ID
        assertNotEquals(
            "Channel ID must be versioned away from 'reminders_alarm' which had sound configured",
            "reminders_alarm",
            NotificationUtil.CHANNEL_ALARM
        )
    }

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
