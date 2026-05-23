package com.example.minlishlite.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeNewWordsPerDay(): Flow<Int>
    suspend fun saveNewWordsPerDay(count: Int)
    fun observeReminderEnabled(): Flow<Boolean>
    suspend fun saveReminderEnabled(enabled: Boolean)
    fun observeReminderTime(): Flow<String>
    suspend fun saveReminderTime(time: String)
}
