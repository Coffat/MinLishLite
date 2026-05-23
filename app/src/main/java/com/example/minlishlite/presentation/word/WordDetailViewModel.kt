package com.example.minlishlite.presentation.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.domain.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WordDetailUiState(
    val word: Word? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WordDetailViewModel(
    private val wordRepository: WordRepository,
    private val wordId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordDetailUiState())
    val uiState: StateFlow<WordDetailUiState> = _uiState.asStateFlow()

    init {
        loadWordDetails()
    }

    fun loadWordDetails() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val word = wordRepository.getWordById(wordId)
                if (word != null) {
                    _uiState.update { it.copy(word = word, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Không tìm thấy từ vựng.", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Lỗi khi tải thông tin từ: ${e.localizedMessage}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onDeleteWord(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                wordRepository.deleteWordById(wordId)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Không thể xóa từ vựng: ${e.localizedMessage}") }
            }
        }
    }

    companion object {
        fun provideFactory(wordId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication)
                WordDetailViewModel(
                    wordRepository = application.container.wordRepository,
                    wordId = wordId
                )
            }
        }
    }
}
