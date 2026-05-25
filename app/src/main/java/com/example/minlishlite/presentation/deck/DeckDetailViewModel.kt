package com.example.minlishlite.presentation.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.data.repository.DeckRepository
import com.example.minlishlite.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class WordFilter {
    ALL, DUE_TODAY, LEARNED
}

data class DeckDetailUiState(
    val deck: DeckEntity? = null,
    val words: List<WordEntity> = emptyList(),
    val totalWordsCount: Int = 0,
    val dueWordsCount: Int = 0,
    val learnedWordsCount: Int = 0,
    val searchQuery: String = "",
    val selectedFilter: WordFilter = WordFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DeckDetailViewModel(
    private val deckRepository: DeckRepository,
    private val wordRepository: WordRepository,
    private val deckId: Int
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(WordFilter.ALL)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    // Flow for the deck details
    private val _deckFlow = MutableStateFlow<DeckEntity?>(null)

    init {
        loadDeckDetails()
    }

    private fun loadDeckDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val deck = deckRepository.getDeckById(deckId)
                if (deck != null) {
                    _deckFlow.value = deck
                } else {
                    _error.value = "Không tìm thấy bộ từ vựng."
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải thông tin bộ từ: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    val uiState: StateFlow<DeckDetailUiState> = combine(
        _deckFlow,
        wordRepository.observeWordsByDeckId(deckId),
        _searchQuery,
        _selectedFilter,
        _isLoading,
        _error
    ) { flowsArray ->
        val deck = flowsArray[0] as DeckEntity?
        @Suppress("UNCHECKED_CAST")
        val allWords = flowsArray[1] as List<WordEntity>
        val query = flowsArray[2] as String
        val filter = flowsArray[3] as WordFilter
        val loading = flowsArray[4] as Boolean
        val error = flowsArray[5] as String?

        val currentTime = System.currentTimeMillis()
        
        // Calculate counts
        val totalCount = allWords.size
        val dueCount = allWords.count { it.nextReviewAt <= currentTime }
        val learnedCount = allWords.count { it.reviewCount > 0 }

        // Filter and search logic
        val filteredWords = allWords.filter { wordItem ->
            val matchesSearch = wordItem.word.contains(query, ignoreCase = true) ||
                    wordItem.meaning.contains(query, ignoreCase = true) ||
                    wordItem.description.contains(query, ignoreCase = true)
            
            val matchesFilter = when (filter) {
                WordFilter.ALL -> true
                WordFilter.DUE_TODAY -> wordItem.nextReviewAt <= currentTime
                WordFilter.LEARNED -> wordItem.reviewCount > 0
            }
            matchesSearch && matchesFilter
        }

        DeckDetailUiState(
            deck = deck,
            words = filteredWords,
            totalWordsCount = totalCount,
            dueWordsCount = dueCount,
            learnedWordsCount = learnedCount,
            searchQuery = query,
            selectedFilter = filter,
            isLoading = loading,
            error = error
        )
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeckDetailUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelect(filter: WordFilter) {
        _selectedFilter.value = filter
    }

    fun onDeleteWord(wordId: Int) {
        viewModelScope.launch {
            try {
                wordRepository.deleteWordById(wordId)
            } catch (e: Exception) {
                _error.value = "Không thể xóa từ: ${e.localizedMessage}"
            }
        }
    }

    fun importCsv(csvContent: String, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val words = com.example.minlishlite.core.util.CsvHelper.parseCsv(csvContent, deckId)
                if (words.isEmpty()) {
                    onError("Không tìm thấy từ vựng hợp lệ để nhập.")
                    return@launch
                }
                words.forEach { word ->
                    wordRepository.insertWord(word)
                }
                onSuccess(words.size)
            } catch (e: Exception) {
                onError("Lỗi khi nhập dữ liệu: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun exportCsv(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val words = wordRepository.getWordsByDeckIdDirect(deckId)
                val csvContent = com.example.minlishlite.core.util.CsvHelper.exportToCsv(words)
                onSuccess(csvContent)
            } catch (e: Exception) {
                onError("Lỗi khi xuất dữ liệu: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        fun provideFactory(deckId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication)
                DeckDetailViewModel(
                    deckRepository = application.container.deckRepository,
                    wordRepository = application.container.wordRepository,
                    deckId = deckId
                )
            }
        }
    }
}
