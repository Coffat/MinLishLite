package com.example.minlishlite.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.domain.repository.DeckRepository
import com.example.minlishlite.domain.repository.WordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ReviewTodayWordItem(
    val wordId: Int,
    val word: String,
    val meaning: String,
    val deckName: String
)

data class ReviewTodayUiState(
    val dueWords: List<ReviewTodayWordItem> = emptyList(),
    val dueCount: Int = 0,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewTodayViewModel(
    private val wordRepository: WordRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    val uiState: StateFlow<ReviewTodayUiState> = deckRepository.observeAllDecks()
        .flatMapLatest { decks ->
            val now = System.currentTimeMillis()
            wordRepository.observeWordsDueToday(now).map { dueWords ->
                val deckNameById = decks.associate { it.id to it.name }
                val items = dueWords.map { word ->
                    ReviewTodayWordItem(
                        wordId = word.id,
                        word = word.word,
                        meaning = word.meaning,
                        deckName = deckNameById[word.deckId] ?: "Bộ từ"
                    )
                }
                ReviewTodayUiState(
                    dueWords = items,
                    dueCount = items.size,
                    isLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReviewTodayUiState(isLoading = true)
        )

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication
                ReviewTodayViewModel(
                    wordRepository = application.container.wordRepository,
                    deckRepository = application.container.deckRepository
                )
            }
        }
    }
}
