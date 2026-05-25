package com.example.minlishlite.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.data.repository.DeckRepository
import com.example.minlishlite.data.repository.ProgressRepository
import com.example.minlishlite.data.repository.SettingsRepository
import com.example.minlishlite.data.repository.UserRepository
import com.example.minlishlite.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

class HomeViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val wordRepository: WordRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var _unreviewedCount: Int = 0
    private var _decks: List<DeckEntity> = emptyList()
    private var _dueWordsList: List<WordEntity> = emptyList()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            // Collect all required data sequentially or via flow collections
            launch {
                userRepository.observeUser().collect { user ->
                    _uiState.update { it.copy(userName = user?.name?.takeIf { name -> name.isNotBlank() } ?: "bạn") }
                }
            }

            launch {
                settingsRepository.observeNewWordsPerDay().collect { newWordsPerDay ->
                    _uiState.update { it.copy(newWordsPerDay = newWordsPerDay) }
                    updateSuggestedWords()
                }
            }

            launch {
                wordRepository.observeUnreviewedWordsCount().collect { unreviewedCount ->
                    _unreviewedCount = unreviewedCount
                    updateSuggestedWords()
                }
            }

            launch {
                progressRepository.observeTotalWordsLearned().collect { wordsLearned ->
                    _uiState.update { it.copy(wordsLearned = wordsLearned) }
                }
            }

            launch {
                deckRepository.observeAllDecks().collect { decks ->
                    _decks = decks
                    _uiState.update { state ->
                        state.copy(
                            deckPreviews = decks.take(3).map { DeckPreview(it.id, it.name, it.tag) }
                        )
                    }
                    updateDueWordsPreview()
                }
            }

            launch {
                val now = System.currentTimeMillis()
                wordRepository.observeWordsDueToday(now).collect { dueWords ->
                    _dueWordsList = dueWords
                    updateDueWordsPreview()
                }
            }

            launch {
                val now = System.currentTimeMillis()
                progressRepository.observeDueTodayCount(now).collect { dueCount ->
                    _uiState.update { it.copy(dueTodayCount = dueCount) }
                }
            }

            // Once launch registers collection, we can toggle off initial loading state
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun updateSuggestedWords() {
        _uiState.update { state ->
            state.copy(
                newWordsSuggested = min(_unreviewedCount, state.newWordsPerDay)
            )
        }
    }

    private fun updateDueWordsPreview() {
        val deckNameById = _decks.associate { it.id to it.name }
        _uiState.update { state ->
            state.copy(
                dueWordsPreview = _dueWordsList.take(5).map { word ->
                    DueWordItem(
                        wordId = word.id,
                        word = word.word,
                        meaning = word.meaning,
                        deckName = deckNameById[word.deckId] ?: "Bộ từ"
                    )
                }
            )
        }
    }

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
