package com.example.minlishlite.core.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Quản lý lịch nhắc học mỗi ngày dùng WorkManager.
 *
 * Cách hoạt động:
 * 1. Người dùng cài đặt giờ nhắc trong Settings (ví dụ "09:00")
 * 2. [schedule] tính khoảng thời gian đến lần nhắc đầu tiên (initialDelay)
 * 3. WorkManager lên lịch chạy Worker vào giờ đó, lặp lại mỗi 24 giờ
 * 4. Khi người dùng tắt nhắc → [cancel] hủy lịch
 */
object StudyReminderScheduler {

    private const val WORK_NAME = "study_reminder_daily"
    fun schedule(context: Context, hour: Int, minute: Int) {
        // Tính thời gian chờ đến lần nhắc đầu tiên
        val initialDelayMs = calculateInitialDelay(hour, minute)

        // Tạo yêu cầu chạy định kỳ: mỗi 24 giờ
        val workRequest = PeriodicWorkRequestBuilder<StudyReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .build()

        // Dùng REPLACE để cập nhật giờ nếu người dùng thay đổi trong Settings
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }


    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()

        // Đặt target = hôm nay lúc giờ:phút đã cài đặt
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Nếu giờ target đã qua trong hôm nay → nhắc vào ngày mai
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }


    fun parseTime(timeString: String): Pair<Int, Int> {
        return try {
            val parts = timeString.split(":")
            val hour = parts[0].trim().toInt()
            val minute = parts[1].trim().toInt()
            Pair(hour, minute)
        } catch (e: Exception) {
            // Nếu chuỗi lỗi định dạng → fallback về 09:00
            Pair(9, 0)
        }
    }
}
