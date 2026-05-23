package com.example.minlishlite.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.core.notification.StudyNotificationHelper
import kotlinx.coroutines.flow.first

class StudyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? MinLishApplication ?: return Result.failure()
        val container = app.container

        val reminderEnabled = container.settingsRepository.observeReminderEnabled().first()
        if (!reminderEnabled) {
            container.studyReminderScheduler.cancel()
            return Result.success()
        }

        val reminderTime = container.settingsRepository.observeReminderTime().first()
        val now = System.currentTimeMillis()
        val dueCount = container.progressRepository.observeDueTodayCount(now).first()

        StudyNotificationHelper.showStudyReminder(
            context = applicationContext,
            dueTodayCount = dueCount,
            reminderTime = reminderTime
        )

        container.studyReminderScheduler.schedule(reminderTime)
        return Result.success()
    }
}
