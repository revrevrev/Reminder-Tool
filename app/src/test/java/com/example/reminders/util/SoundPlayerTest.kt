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
import java.io.IOException

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
        SoundPlayer::class.java.getDeclaredField("mediaPlayer").also {
            it.isAccessible = true; it.set(SoundPlayer, null)
        }
        SoundPlayer::class.java.getDeclaredField("currentPreviewSoundId").also {
            it.isAccessible = true; it.set(SoundPlayer, null)
        }
    }

    /**
     * Regression test for: placeholder OGG files (invalid audio) cause prepare() to throw
     * IOException, which propagates uncaught and crashes the app.
     *
     * The fix must catch the exception, release the MediaPlayer, and leave mediaPlayer null.
     */
    @Test
    fun `preview does not crash when prepare throws IOException`() {
        mockConstruction(AudioAttributes.Builder::class.java) { builder, _ ->
            whenever(builder.setUsage(any<Int>())).thenReturn(builder)
            whenever(builder.setContentType(any<Int>())).thenReturn(builder)
            doReturn(null).whenever(builder).build()
        }.use {
            doThrow(IOException("invalid audio data")).whenever(mockMp1).prepare()

            // Must NOT propagate — app must not crash on invalid/placeholder audio files
            SoundPlayer.preview(mockContext, "chime", 70)

            // Player must be released and not left dangling
            verify(mockMp1).release()
            // start() must NOT have been called on a player that never prepared
            verify(mockMp1, never()).start()
        }
    }

    /**
     * Regression test for defect 10: clicking the play triangle on a sound that is already
     * previewing should stop it (toggle off), not stop-and-restart it.
     *
     * Before fix: preview("chime") while "chime" is playing calls stop() then starts a NEW player.
     * User hears the sound restart instead of stop, and when both onPreview + onSelect fire for
     * the same tap (Compose propagation) the rapid stop-restart sequence can also produce silence.
     *
     * After fix: if the same soundId is currently playing, preview() stops it and returns.
     */
    @Test
    fun `preview of same sound while already playing stops instead of restarting`() {
        mockConstruction(AudioAttributes.Builder::class.java) { builder, _ ->
            whenever(builder.setUsage(any<Int>())).thenReturn(builder)
            whenever(builder.setContentType(any<Int>())).thenReturn(builder)
            doReturn(null).whenever(builder).build()
        }.use {
            // Start previewing "chime"
            SoundPlayer.preview(mockContext, "chime", 70)
            verify(mockMp1).start()

            // Click the play button again for the same sound — should toggle OFF
            SoundPlayer.preview(mockContext, "chime", 70)

            // FAILS before fix: mockMp2.start() is called because preview() always restarts
            // PASSES after fix: same sound detected, stop() called, no new player created
            verify(mockMp1, times(1)).stop()   // original player was stopped
            verify(mockMp2, never()).start()    // no second player was started
        }
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
