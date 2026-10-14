package com.example.reminders.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.example.reminders.R

object SoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

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
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(context.resources.openRawResourceFd(soundResId(soundId)))
            setVolume(volume / 100f, volume / 100f)
            isLooping = false
            prepare()
            start()
            setOnCompletionListener { release() }
        }
    }

    fun startAlarm(context: Context, soundId: String, volume: Int) {
        stop()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(context.resources.openRawResourceFd(soundResId(soundId)))
            setVolume(volume / 100f, volume / 100f)
            isLooping = true
            prepare()
            start()
        }
    }

    fun stop() {
        mediaPlayer?.runCatching { stop(); release() }
        mediaPlayer = null
    }
}
