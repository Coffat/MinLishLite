package com.example.minlishlite.core.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

object PronunciationAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, url: String) {
        stop()
        if (url.isBlank()) return

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            setOnPreparedListener { it.start() }
            setOnCompletionListener { stop() }
            prepareAsync()
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
