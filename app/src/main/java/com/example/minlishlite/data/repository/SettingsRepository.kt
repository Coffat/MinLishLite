package com.example.minlishlite.data.repository

import android.content.Context
import com.example.minlishlite.data.local.preference.SettingsPreferences
import kotlinx.coroutines.flow.Flow

class SettingsRepository(context: Context) {

    private val preferences = SettingsPreferences(context.applicationContext)

    suspend fun ensureMigrated() {
        preferences.migrateFromLegacySharedPreferences()
    }

    fun observeNewWordsPerDay(): Flow<Int> = preferences.observeNewWordsPerDay()

    suspend fun saveNewWordsPerDay(count: Int) {
        preferences.setNewWordsPerDay(count)
    }

    fun observeReminderEnabled(): Flow<Boolean> = preferences.observeReminderEnabled()

    suspend fun saveReminderEnabled(enabled: Boolean) {
        preferences.setReminderEnabled(enabled)
    }

    fun observeReminderTime(): Flow<String> = preferences.observeReminderTime()

    suspend fun saveReminderTime(time: String) {
        preferences.setReminderTime(time)
    }
}
