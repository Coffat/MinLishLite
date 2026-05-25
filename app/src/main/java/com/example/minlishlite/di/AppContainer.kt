package com.example.minlishlite.di

import android.content.Context
import com.example.minlishlite.BuildConfig
import com.example.minlishlite.data.local.database.AppDatabase
import com.example.minlishlite.data.remote.api.DictionaryApiService
import com.example.minlishlite.data.remote.api.TranslationApiService
import com.example.minlishlite.data.repository.DeckRepository
import com.example.minlishlite.data.repository.DictionaryRepository
import com.example.minlishlite.data.repository.ProgressRepository
import com.example.minlishlite.data.repository.SettingsRepository
import com.example.minlishlite.data.repository.StudyRepository
import com.example.minlishlite.data.repository.TranslationRepository
import com.example.minlishlite.data.repository.UserRepository
import com.example.minlishlite.data.repository.WordRepository

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
        TranslationRepository(translationApiService)
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    override val deckRepository: DeckRepository by lazy {
        DeckRepository(database.deckDao())
    }

    override val wordRepository: WordRepository by lazy {
        WordRepository(database.wordDao())
    }

    override val studyRepository: StudyRepository by lazy {
        StudyRepository(database.wordDao(), database.reviewHistoryDao())
    }

    override val progressRepository: ProgressRepository by lazy {
        ProgressRepository(database.wordDao(), database.reviewHistoryDao())
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(context)
    }

    override val dictionaryRepository: DictionaryRepository by lazy {
        DictionaryRepository(dictionaryApiService, translationRepository)
    }

}
