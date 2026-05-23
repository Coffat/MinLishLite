package com.example.minlishlite.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

object NotificationChannels {

    const val STUDY_REMINDER_CHANNEL_ID = "study_reminder"
    const val STUDY_REMINDER_CHANNEL_NAME = "Nhắc học từ vựng"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            STUDY_REMINDER_CHANNEL_ID,
            STUDY_REMINDER_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Thông báo nhắc ôn tập từ vựng hằng ngày"
        }

        val manager = context.getSystemService<NotificationManager>()
        manager?.createNotificationChannel(channel)
    }
}
