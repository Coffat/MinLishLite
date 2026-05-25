package com.example.minlishlite.presentation.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.data.model.ReviewResult
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.data.repository.DeckRepository
import com.example.minlishlite.data.repository.StudyRepository
import com.example.minlishlite.data.repository.WordRepository
import com.example.minlishlite.core.util.SrsCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudyUiState(
    val deckName: String = "",
    val currentWord: WordEntity? = null,
    val currentIndex: Int = 0,
    val totalCount: Int = 0,
    val progressLabel: String = "0/0",
    val progressFraction: Float = 0f,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true,
    val isSubmittingRating: Boolean = false,
    val errorMessage: String? = null,
    val isSessionComplete: Boolean = false
)

class StudyViewModel(
    private val deckRepository: DeckRepository,
    private val wordRepository: WordRepository,
    private val studyRepository: StudyRepository,
    private val studyMode: StudyMode
) : ViewModel() {

    private val _deckName = MutableStateFlow("")
    private val _sessionWords = MutableStateFlow<List<WordEntity>?>(null)
    private val _currentIndex = MutableStateFlow(0)
    private val _isFlipped = MutableStateFlow(false)
    private val _isSessionComplete = MutableStateFlow(false)
    private val _isSubmittingRating = MutableStateFlow(false)
    private val _deckLoaded = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val dueWordsFlow: Flow<List<WordEntity>> = when (studyMode) {
        is StudyMode.DeckDue -> wordRepository.observeWordsByDeckId(studyMode.deckId).map { words ->
            val now = System.currentTimeMillis()
            words
                .filter { it.nextReviewAt <= now }
                .sortedWith(compareBy({ it.nextReviewAt }, { it.word.lowercase() }))
        }
        StudyMode.DueToday -> wordRepository.observeWordsDueToday(System.currentTimeMillis()).map { words ->
            words.sortedWith(compareBy({ it.nextReviewAt }, { it.word.lowercase() }))
        }
    }

    init {
        when (studyMode) {
            is StudyMode.DeckDue -> {
                viewModelScope.launch {
                    try {
                        val deck = deckRepository.getDeckById(studyMode.deckId)
                        if (deck != null) {
                            _deckName.value = deck.name
                        } else {
                            _error.value = "Không tìm thấy bộ từ vựng."
                        }
                    } catch (e: Exception) {
                        _error.value = "Lỗi khi tải bộ từ: ${e.localizedMessage}"
                    } finally {
                        _deckLoaded.value = true
                    }
                }
            }
            StudyMode.DueToday -> {
                _deckName.value = "Ôn tập hôm nay"
                _deckLoaded.value = true
            }
        }

        viewModelScope.launch {
            dueWordsFlow.collect { dueWords ->
                if (_sessionWords.value == null && !_isSessionComplete.value) {
                    _sessionWords.value = dueWords
                }
            }
        }
    }

    val uiState: StateFlow<StudyUiState> = combine(
        _deckName,
        _sessionWords,
        _currentIndex,
        _isFlipped,
        _isSessionComplete,
        _isSubmittingRating,
        _deckLoaded,
        _error
    ) { values ->
        val deckName = values[0] as String
        @Suppress("UNCHECKED_CAST")
        val sessionWords = values[1] as List<WordEntity>?
        val currentIndex = values[2] as Int
        val isFlipped = values[3] as Boolean
        val isSessionComplete = values[4] as Boolean
        val isSubmittingRating = values[5] as Boolean
        val deckLoaded = values[6] as Boolean
        val error = values[7] as String?

        val words = sessionWords ?: emptyList()
        val totalCount = words.size
        val safeIndex = currentIndex.coerceIn(0, (totalCount - 1).coerceAtLeast(0))

        StudyUiState(
            deckName = deckName,
            currentWord = words.getOrNull(safeIndex),
            currentIndex = safeIndex,
            totalCount = totalCount,
            progressLabel = if (totalCount == 0) "0/0" else "${safeIndex + 1}/$totalCount",
            progressFraction = if (totalCount == 0) 0f else (safeIndex + 1f) / totalCount,
            isFlipped = isFlipped,
            isLoading = !deckLoaded || sessionWords == null,
            isSubmittingRating = isSubmittingRating,
            errorMessage = error,
            isSessionComplete = isSessionComplete
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StudyUiState(isLoading = true)
    )

    fun onFlipCard() {
        if (_isSessionComplete.value || _isSubmittingRating.value) return
        _isFlipped.update { flipped ->
            if (!flipped) true else flipped
        }
    }

    fun onRateCard(result: ReviewResult) {
        if (_isSessionComplete.value || !_isFlipped.value || _isSubmittingRating.value) return

        val words = _sessionWords.value ?: return
        val currentIndex = _currentIndex.value
        val word = words.getOrNull(currentIndex) ?: return

        viewModelScope.launch {
            _isSubmittingRating.value = true
            try {
                val now = System.currentTimeMillis()
                val outcome = SrsCalculator.applyReview(word.easeFactor, result, now)
                studyRepository.reviewWord(
                    wordId = word.id,
                    result = result,
                    nextReviewAt = outcome.nextReviewAt,
                    easeFactor = outcome.easeFactor
                )

                if (currentIndex < words.lastIndex) {
                    _currentIndex.value = currentIndex + 1
                    _isFlipped.value = false
                } else {
                    _isSessionComplete.value = true
                }
            } catch (e: Exception) {
                _error.value = "Không thể lưu kết quả ôn tập: ${e.localizedMessage}"
            } finally {
                _isSubmittingRating.value = false
            }
        }
    }

    companion object {
        fun provideFactory(studyMode: StudyMode): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication
                StudyViewModel(
                    deckRepository = application.container.deckRepository,
                    wordRepository = application.container.wordRepository,
                    studyRepository = application.container.studyRepository,
                    studyMode = studyMode
                )
            }
        }

        fun provideFactory(deckId: Int): ViewModelProvider.Factory =
            provideFactory(StudyMode.DeckDue(deckId))
    }
}
