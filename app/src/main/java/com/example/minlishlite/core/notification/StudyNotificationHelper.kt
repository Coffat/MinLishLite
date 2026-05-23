package com.example.minlishlite.core.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.minlishlite.MainActivity
import com.example.minlishlite.R

object StudyNotificationHelper {

    const val STUDY_REMINDER_NOTIFICATION_ID = 1001

    fun showStudyReminder(
        context: Context,
        dueTodayCount: Int,
        reminderTime: String
    ) {
        val title = "MinLish Lite — Nhắc học"
        val content = if (dueTodayCount > 0) {
            "Bạn có $dueTodayCount từ cần ôn hôm nay. Giờ nhắc: $reminderTime"
        } else {
            "Đã đến giờ ôn từ vựng ($reminderTime). Hãy duy trì thói quen học mỗi ngày!"
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.STUDY_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(STUDY_REMINDER_NOTIFICATION_ID, notification)
    }
}
