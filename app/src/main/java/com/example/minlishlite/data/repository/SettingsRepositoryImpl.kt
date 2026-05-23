package com.example.minlishlite.data.repository

import android.content.Context
import com.example.minlishlite.data.local.preference.SettingsPreferences
import com.example.minlishlite.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val preferences = SettingsPreferences(context.applicationContext)

    suspend fun ensureMigrated() {
        preferences.migrateFromLegacySharedPreferences()
    }

    override fun observeNewWordsPerDay(): Flow<Int> = preferences.observeNewWordsPerDay()

    override suspend fun saveNewWordsPerDay(count: Int) {
        preferences.setNewWordsPerDay(count)
    }

    override fun observeReminderEnabled(): Flow<Boolean> = preferences.observeReminderEnabled()

    override suspend fun saveReminderEnabled(enabled: Boolean) {
        preferences.setReminderEnabled(enabled)
    }

    override fun observeReminderTime(): Flow<String> = preferences.observeReminderTime()

    override suspend fun saveReminderTime(time: String) {
        preferences.setReminderTime(time)
    }
}
