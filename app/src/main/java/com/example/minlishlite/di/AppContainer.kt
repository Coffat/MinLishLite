package com.example.minlishlite.di

import android.content.Context
import com.example.minlishlite.BuildConfig
import com.example.minlishlite.data.local.database.AppDatabase
import com.example.minlishlite.data.remote.api.DictionaryApiService
import com.example.minlishlite.data.remote.api.TranslationApiService
import com.example.minlishlite.data.repository.DeckRepositoryImpl
import com.example.minlishlite.data.repository.DictionaryRepositoryImpl
import com.example.minlishlite.data.repository.ProgressRepositoryImpl
import com.example.minlishlite.core.notification.StudyReminderSchedulerImpl
import com.example.minlishlite.data.repository.SettingsRepositoryImpl
import com.example.minlishlite.data.repository.StudyRepositoryImpl
import com.example.minlishlite.data.repository.TranslationRepositoryImpl
import com.example.minlishlite.data.repository.UserRepositoryImpl
import com.example.minlishlite.data.repository.WordRepositoryImpl
import com.example.minlishlite.core.notification.StudyReminderScheduler
import com.example.minlishlite.domain.repository.DeckRepository
import com.example.minlishlite.domain.repository.DictionaryRepository
import com.example.minlishlite.domain.repository.ProgressRepository
import com.example.minlishlite.domain.repository.SettingsRepository
import com.example.minlishlite.domain.repository.StudyRepository
import com.example.minlishlite.domain.repository.TranslationRepository
import com.example.minlishlite.domain.repository.UserRepository
import com.example.minlishlite.domain.repository.WordRepository
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val userRepository: UserRepository
    val deckRepository: DeckRepository
    val wordRepository: WordRepository
    val studyRepository: StudyRepository
    val progressRepository: ProgressRepository
    val settingsRepository: SettingsRepository
    val dictionaryRepository: DictionaryRepository
    val studyReminderScheduler: StudyReminderScheduler
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        }
                    )
                }
            }
            .build()
    }

    private val dictionaryRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val translationRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mymemory.translated.net/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val dictionaryApiService: DictionaryApiService by lazy {
        dictionaryRetrofit.create(DictionaryApiService::class.java)
    }

    private val translationApiService: TranslationApiService by lazy {
        translationRetrofit.create(TranslationApiService::class.java)
    }

    private val translationRepository: TranslationRepository by lazy {
        TranslationRepositoryImpl(translationApiService)
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
        DictionaryRepositoryImpl(dictionaryApiService, translationRepository)
    }

    override val studyReminderScheduler: StudyReminderScheduler by lazy {
        StudyReminderSchedulerImpl(context.applicationContext)
    }
}
