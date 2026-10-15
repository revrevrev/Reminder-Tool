package com.example.reminders.util

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.Resources
import android.media.AudioAttributes
import android.media.MediaPlayer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mockConstruction
import org.mockito.kotlin.*

class SoundPlayerTest {

    private lateinit var mockMp1: MediaPlayer
    private lateinit var mockMp2: MediaPlayer
    private lateinit var mockContext: Context
    private lateinit var mockResources: Resources
    private lateinit var mockAfd: AssetFileDescriptor
    private var callCount = 0

    @Before
    fun setUp() {
        mockMp1 = mock()
        mockMp2 = mock()
        mockContext = mock()
        mockResources = mock()
        mockAfd = mock()
        callCount = 0

        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(mockResources.openRawResourceFd(any<Int>())).thenReturn(mockAfd)

        SoundPlayer.playerFactory = { if (callCount++ == 0) mockMp1 else mockMp2 }
    }

    @After
    fun tearDown() {
        SoundPlayer.playerFactory = { MediaPlayer() }
        val field = SoundPlayer::class.java.getDeclaredField("mediaPlayer")
        field.isAccessible = true
        field.set(SoundPlayer, null)
    }

    /**
     * Regression test for: picking a different sound after the first sound finishes
     * playing naturally causes a native crash (double MediaPlayer.release()).
     *
     * Sequence:
     *   1. preview("chime") → MediaPlayer created, completion listener registered
     *   2. Sound finishes → completion listener fires, calls release()
     *                        but does NOT null SoundPlayer.mediaPlayer  ← the bug
     *   3. preview("bell") → stop() finds non-null mediaPlayer, calls release() AGAIN → crash
     */
    @Test
    fun `picking second sound after first completes naturally does not double-release`() {
        mockConstruction(AudioAttributes.Builder::class.java) { builder, _ ->
            whenever(builder.setUsage(any<Int>())).thenReturn(builder)
            whenever(builder.setContentType(any<Int>())).thenReturn(builder)
            doReturn(null).whenever(builder).build()
        }.use {
            // Step 1: start playing the first sound
            SoundPlayer.preview(mockContext, "chime", 70)

            // Capture the completion listener registered on the first player
            val captor = argumentCaptor<MediaPlayer.OnCompletionListener>()
            verify(mockMp1).setOnCompletionListener(captor.capture())

            // Step 2: first sound plays to completion naturally
            captor.firstValue.onCompletion(mockMp1)
            verify(mockMp1, times(1)).release()

            // Step 3: user picks a different sound — preview() calls stop() internally
            SoundPlayer.preview(mockContext, "bell", 70)

            // release() must NOT be called again (total must remain 1)
            // FAILS with bug  (stop() calls release() on still-referenced completed player)
            // PASSES with fix (completion listener nulled mediaPlayer → stop() is a no-op)
            verify(mockMp1, times(1)).release()
        }
    }
}
