package com.example.minlishlite.di

import android.content.Context
import com.example.minlishlite.data.local.database.AppDatabase
import com.example.minlishlite.data.repository.DeckRepositoryImpl
import com.example.minlishlite.data.repository.DictionaryRepositoryImpl
import com.example.minlishlite.data.repository.ProgressRepositoryImpl
import com.example.minlishlite.data.repository.SettingsRepositoryImpl
import com.example.minlishlite.data.repository.StudyRepositoryImpl
import com.example.minlishlite.data.repository.UserRepositoryImpl
import com.example.minlishlite.data.repository.WordRepositoryImpl
import com.example.minlishlite.domain.repository.DeckRepository
import com.example.minlishlite.domain.repository.DictionaryRepository
import com.example.minlishlite.domain.repository.ProgressRepository
import com.example.minlishlite.domain.repository.SettingsRepository
import com.example.minlishlite.domain.repository.StudyRepository
import com.example.minlishlite.domain.repository.UserRepository
import com.example.minlishlite.domain.repository.WordRepository

interface AppContainer {
    val userRepository: UserRepository
    val deckRepository: DeckRepository
    val wordRepository: WordRepository
    val studyRepository: StudyRepository
    val progressRepository: ProgressRepository
    val settingsRepository: SettingsRepository
    val dictionaryRepository: DictionaryRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(database.userDao())
    }

    override val deckRepository: DeckRepository by lazy {
        DeckRepositoryImpl(database.deckDao())
    }

    override val wordRepository: WordRepository by lazy {
        WordRepositoryImpl(database.wordDao())
    }

    override val studyRepository: StudyRepository by lazy {
        StudyRepositoryImpl(database.wordDao(), database.reviewHistoryDao())
    }

    override val progressRepository: ProgressRepository by lazy {
        ProgressRepositoryImpl(database.wordDao(), database.reviewHistoryDao())
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(context)
    }

    override val dictionaryRepository: DictionaryRepository by lazy {
        DictionaryRepositoryImpl()
    }
}
