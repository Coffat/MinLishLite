package com.example.minlishlite

import android.app.Application
import com.example.minlishlite.core.notification.NotificationChannels
import com.example.minlishlite.data.repository.SettingsRepositoryImpl
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
        NotificationChannels.create(this)
        bootstrapSettingsAndReminders()
    }

    private fun bootstrapSettingsAndReminders() {
        applicationScope.launch {
            val settingsRepo = container.settingsRepository
            if (settingsRepo is SettingsRepositoryImpl) {
                settingsRepo.ensureMigrated()
            }

            val scheduler = container.studyReminderScheduler
            val enabled = settingsRepo.observeReminderEnabled().first()
            if (enabled) {
                val time = settingsRepo.observeReminderTime().first()
                scheduler.schedule(time)
            } else {
                scheduler.cancel()
            }
        }
    }
}
