package com.example.minlishlite.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.minlishlite.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SettingsRepositoryImpl(context: Context) : SettingsRepository {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("minlish_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NEW_WORDS_PER_DAY = "new_words_per_day"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_TIME = "reminder_time"

        private const val DEFAULT_NEW_WORDS_PER_DAY = 10
        private const val DEFAULT_REMINDER_ENABLED = true
        private const val DEFAULT_REMINDER_TIME = "09:00"
    }

    override fun observeNewWordsPerDay(): Flow<Int> = callbackFlow {
        trySend(sharedPreferences.getInt(KEY_NEW_WORDS_PER_DAY, DEFAULT_NEW_WORDS_PER_DAY))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_NEW_WORDS_PER_DAY) {
                trySend(sharedPreferences.getInt(KEY_NEW_WORDS_PER_DAY, DEFAULT_NEW_WORDS_PER_DAY))
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun saveNewWordsPerDay(count: Int) {
        sharedPreferences.edit().putInt(KEY_NEW_WORDS_PER_DAY, count).apply()
    }

    override fun observeReminderEnabled(): Flow<Boolean> = callbackFlow {
        trySend(sharedPreferences.getBoolean(KEY_REMINDER_ENABLED, DEFAULT_REMINDER_ENABLED))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_REMINDER_ENABLED) {
                trySend(sharedPreferences.getBoolean(KEY_REMINDER_ENABLED, DEFAULT_REMINDER_ENABLED))
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun saveReminderEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
    }

    override fun observeReminderTime(): Flow<String> = callbackFlow {
        trySend(sharedPreferences.getString(KEY_REMINDER_TIME, DEFAULT_REMINDER_TIME) ?: DEFAULT_REMINDER_TIME)

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_REMINDER_TIME) {
                trySend(sharedPreferences.getString(KEY_REMINDER_TIME, DEFAULT_REMINDER_TIME) ?: DEFAULT_REMINDER_TIME)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun saveReminderTime(time: String) {
        sharedPreferences.edit().putString(KEY_REMINDER_TIME, time).apply()
    }
}
