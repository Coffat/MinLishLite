package com.example.minlishlite

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.minlishlite.core.worker.StudyReminderScheduler
import com.example.minlishlite.core.worker.StudyReminderWorker
import com.example.minlishlite.di.AppContainer
import com.example.minlishlite.di.AppDataContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MinLishApplication : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        // Tạo kênh notification (bắt buộc trên Android 8+, bỏ qua trên Android 7 trở xuống)
        createNotificationChannel()
        bootstrapSettings()
        bootstrapReminder()
    }

    private fun bootstrapSettings() {
        applicationScope.launch {
            val settingsRepo = container.settingsRepository
            settingsRepo.ensureMigrated()
        }
    }

    /**
     * Khi app khởi động, kiểm tra cài đặt nhắc học:
     * - Nếu người dùng đã bật nhắc → lên lịch WorkManager
     * - Nếu tắt → không làm gì
     *
     * Điều này đảm bảo lịch nhắc được phục hồi sau khi khởi động lại thiết bị.
     */
    private fun bootstrapReminder() {
        applicationScope.launch {
            val settingsRepo = container.settingsRepository
            val reminderEnabled = settingsRepo.observeReminderEnabled().first()
            val reminderTime = settingsRepo.observeReminderTime().first()

            if (reminderEnabled) {
                val (hour, minute) = StudyReminderScheduler.parseTime(reminderTime)
                StudyReminderScheduler.schedule(this@MinLishApplication, hour, minute)
            }
        }
    }

    /**
     * Tạo kênh notification "Nhắc ôn từ vựng".
     * Android 8.0+ yêu cầu phải tạo channel trước khi gửi notification.
     * Trên các phiên bản cũ hơn, hàm này không làm gì.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                StudyReminderWorker.CHANNEL_ID,
                "Nhắc ôn từ vựng",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Thông báo nhắc bạn ôn từ vựng hằng ngày theo lịch đã cài đặt"
            }
            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
