package com.example.reminders.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.example.reminders.R

object SoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    internal var playerFactory: () -> MediaPlayer = { MediaPlayer() }

    fun soundResId(soundId: String): Int = when (soundId) {
        "chime"   -> R.raw.chime
        "bell"    -> R.raw.bell
        "birds"   -> R.raw.birds
        "pulse"   -> R.raw.pulse
        "marimba" -> R.raw.marimba
        "beep"    -> R.raw.beep
        else      -> R.raw.chime
    }

    fun preview(context: Context, soundId: String, volume: Int) {
        stop()
        val mp = playerFactory()
        try {
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            mp.setDataSource(context.resources.openRawResourceFd(soundResId(soundId)))
            mp.setVolume(volume / 100f, volume / 100f)
            mp.isLooping = false
            mp.prepare()
            mp.start()
            mp.setOnCompletionListener { mediaPlayer = null; it.release() }
            mediaPlayer = mp
        } catch (e: Exception) {
            mp.runCatching { release() }
        }
    }

    fun startAlarm(context: Context, soundId: String, volume: Int, continuous: Boolean = true) {
        stop()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0)
        val mp = playerFactory()
        try {
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            mp.setDataSource(context.resources.openRawResourceFd(soundResId(soundId)))
            mp.setVolume(volume / 100f, volume / 100f)
            mp.isLooping = continuous
            mp.prepare()
            mp.start()
        } catch (e: Exception) {
            mp.runCatching { release() }
            return
        }
        if (!continuous) {
            val plays = intArrayOf(1)
            mp.setOnCompletionListener { player ->
                if (plays[0] < 3 && mediaPlayer != null) {
                    plays[0]++
                    player.seekTo(0)
                    player.start()
                } else {
                    mediaPlayer = null
                    player.release()
                }
            }
        }
        mediaPlayer = mp
    }

    fun stop() {
        val mp = mediaPlayer
        mediaPlayer = null
        mp?.runCatching { stop(); release() }
    }
}
