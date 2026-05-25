package com.example.minlishlite

import android.app.Application
import com.example.minlishlite.di.AppContainer
import com.example.minlishlite.di.AppDataContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MinLishApplication : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        bootstrapSettings()
    }

    private fun bootstrapSettings() {
        applicationScope.launch {
            val settingsRepo = container.settingsRepository
            settingsRepo.ensureMigrated()
        }
    }
}
