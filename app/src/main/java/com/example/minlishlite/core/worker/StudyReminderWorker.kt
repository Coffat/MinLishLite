package com.example.minlishlite.core.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.minlishlite.MainActivity
import com.example.minlishlite.R

/**
 * Worker chạy ngầm mỗi ngày để gửi notification nhắc người dùng ôn từ vựng.
 *
 * WorkManager tự động gọi [doWork] vào đúng giờ đã cài đặt.
 * Dùng [CoroutineWorker] để code chạy trong coroutine, không block main thread.
 */
class StudyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        sendStudyReminderNotification(applicationContext)
        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "study_reminder_channel"
        private const val NOTIFICATION_ID = 1001

        /**
         * Gửi notification nhắc ôn từ vựng.
         * Notification có nút nhấn vào để mở thẳng app.
         */
        fun sendStudyReminderNotification(context: Context) {
            // Tạo Intent mở MainActivity khi người dùng nhấn vào notification
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_IMMUTABLE // bắt buộc trên Android 12+
            )

            // Xây dựng nội dung notification
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Đến giờ ôn từ vựng rồi! 📚")
                .setContentText("Bạn có từ cần ôn hôm nay. Học vài phút để không quên nhé!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // tự đóng khi người dùng nhấn vào
                .build()

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }
}
