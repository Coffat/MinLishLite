package com.example.minlishlite.data.local.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "minlish_settings")

object SettingsPreferenceKeys {
    val NEW_WORDS_PER_DAY = intPreferencesKey("new_words_per_day")
    val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
    val REMINDER_TIME = stringPreferencesKey("reminder_time")
    val MIGRATED_TO_DATASTORE = booleanPreferencesKey("migrated_to_datastore")
}

class SettingsPreferences(private val context: Context) {

    private val dataStore = context.settingsDataStore

    fun observeNewWordsPerDay(): Flow<Int> = dataStore.data.map { prefs ->
        prefs[SettingsPreferenceKeys.NEW_WORDS_PER_DAY] ?: DEFAULT_NEW_WORDS_PER_DAY
    }

    fun observeReminderEnabled(): Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SettingsPreferenceKeys.REMINDER_ENABLED] ?: DEFAULT_REMINDER_ENABLED
    }

    fun observeReminderTime(): Flow<String> = dataStore.data.map { prefs ->
        prefs[SettingsPreferenceKeys.REMINDER_TIME] ?: DEFAULT_REMINDER_TIME
    }

    suspend fun setNewWordsPerDay(count: Int) {
        dataStore.edit { it[SettingsPreferenceKeys.NEW_WORDS_PER_DAY] = count }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsPreferenceKeys.REMINDER_ENABLED] = enabled }
    }

    suspend fun setReminderTime(time: String) {
        dataStore.edit { it[SettingsPreferenceKeys.REMINDER_TIME] = time }
    }

    suspend fun isMigrated(): Boolean {
        return dataStore.data.first()[SettingsPreferenceKeys.MIGRATED_TO_DATASTORE] ?: false
    }

    suspend fun markMigrated() {
        dataStore.edit { it[SettingsPreferenceKeys.MIGRATED_TO_DATASTORE] = true }
    }

    suspend fun migrateFromLegacySharedPreferences() {
        if (isMigrated()) return

        val legacy = context.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
        dataStore.edit { prefs ->
            prefs[SettingsPreferenceKeys.NEW_WORDS_PER_DAY] =
                legacy.getInt("new_words_per_day", DEFAULT_NEW_WORDS_PER_DAY)
            prefs[SettingsPreferenceKeys.REMINDER_ENABLED] =
                legacy.getBoolean("reminder_enabled", DEFAULT_REMINDER_ENABLED)
            prefs[SettingsPreferenceKeys.REMINDER_TIME] =
                legacy.getString("reminder_time", DEFAULT_REMINDER_TIME) ?: DEFAULT_REMINDER_TIME
            prefs[SettingsPreferenceKeys.MIGRATED_TO_DATASTORE] = true
        }
    }

    companion object {
        const val DEFAULT_NEW_WORDS_PER_DAY = 10
        const val DEFAULT_REMINDER_ENABLED = true
        const val DEFAULT_REMINDER_TIME = "09:00"
        const val LEGACY_PREFS_NAME = "minlish_settings"
    }
}
