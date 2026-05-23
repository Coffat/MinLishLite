package com.example.minlishlite.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.domain.model.User
import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.domain.repository.DeckRepository
import com.example.minlishlite.domain.repository.ProgressRepository
import com.example.minlishlite.domain.repository.SettingsRepository
import com.example.minlishlite.domain.repository.UserRepository
import com.example.minlishlite.domain.repository.WordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlin.math.min

data class DueWordItem(
    val wordId: Int,
    val word: String,
    val meaning: String,
    val deckName: String
)

data class DeckPreview(
    val id: Int,
    val name: String,
    val tag: String
)

data class HomeUiState(
    val userName: String = "",
    val dueTodayCount: Int = 0,
    val newWordsSuggested: Int = 0,
    val newWordsPerDay: Int = 10,
    val wordsLearned: Int = 0,
    val dueWordsPreview: List<DueWordItem> = emptyList(),
    val deckPreviews: List<DeckPreview> = emptyList(),
    val isLoading: Boolean = true
)

private data class HomeBaseData(
    val user: User?,
    val newWordsPerDay: Int,
    val unreviewedCount: Int,
    val wordsLearned: Int,
    val decks: List<Deck>
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val wordRepository: WordRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        userRepository.observeUser(),
        settingsRepository.observeNewWordsPerDay(),
        wordRepository.observeUnreviewedWordsCount(),
        progressRepository.observeTotalWordsLearned(),
        deckRepository.observeAllDecks()
    ) { user, newWordsPerDay, unreviewedCount, wordsLearned, decks ->
        HomeBaseData(user, newWordsPerDay, unreviewedCount, wordsLearned, decks)
    }.flatMapLatest { base ->
        val now = System.currentTimeMillis()
        combine(
            wordRepository.observeWordsDueToday(now),
            progressRepository.observeDueTodayCount(now)
        ) { dueWords, dueCount ->
            val deckNameById = base.decks.associate { it.id to it.name }
            HomeUiState(
                userName = base.user?.name?.takeIf { it.isNotBlank() } ?: "bạn",
                dueTodayCount = dueCount,
                newWordsSuggested = min(base.unreviewedCount, base.newWordsPerDay),
                newWordsPerDay = base.newWordsPerDay,
                wordsLearned = base.wordsLearned,
                dueWordsPreview = dueWords.take(5).map { word ->
                    DueWordItem(
                        wordId = word.id,
                        word = word.word,
                        meaning = word.meaning,
                        deckName = deckNameById[word.deckId] ?: "Bộ từ"
                    )
                },
                deckPreviews = base.decks.take(3).map { DeckPreview(it.id, it.name, it.tag) },
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication
                HomeViewModel(
                    userRepository = application.container.userRepository,
                    settingsRepository = application.container.settingsRepository,
                    progressRepository = application.container.progressRepository,
                    wordRepository = application.container.wordRepository,
                    deckRepository = application.container.deckRepository
                )
            }
        }
    }
}
