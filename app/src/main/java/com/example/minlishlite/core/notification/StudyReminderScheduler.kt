package com.example.minlishlite.core.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.minlishlite.data.worker.StudyReminderWorker
import java.util.concurrent.TimeUnit

interface StudyReminderScheduler {
    fun schedule(reminderTime: String)
    fun cancel()
}

class StudyReminderSchedulerImpl(
    private val context: Context
) : StudyReminderScheduler {

    private val workManager = WorkManager.getInstance(context)

    override fun schedule(reminderTime: String) {
        val delayMillis = ReminderTimeCalculator
            .delayUntilNextReminder(reminderTime)
            .toMillis()
            .coerceAtLeast(0L)

        val request = OneTimeWorkRequestBuilder<StudyReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .addTag(WORK_TAG)
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    override fun cancel() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    companion object {
        const val UNIQUE_WORK_NAME = "daily_study_reminder"
        const val WORK_TAG = "study_reminder"
    }
}
