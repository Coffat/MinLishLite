package com.example.minlishlite.presentation.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.domain.repository.DictionaryRepository
import com.example.minlishlite.domain.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditWordUiState(
    val word: String = "",
    val pronunciation: String = "",
    val meaning: String = "",
    val description: String = "",
    val example: String = "",
    val collocation: String = "",
    val relatedWords: String = "",
    val note: String = "",
    val level: String = "Beginner",
    val isSaving: Boolean = false,
    val isSearching: Boolean = false,
    val wordError: String? = null,
    val meaningError: String? = null,
    val searchError: String? = null,
    val searchSuccessMsg: String? = null
)

class AddEditWordViewModel(
    private val wordRepository: WordRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val wordId: Int?,
    private val deckId: Int?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditWordUiState())
    val uiState: StateFlow<AddEditWordUiState> = _uiState.asStateFlow()

    init {
        loadWordDetails()
    }

    private fun loadWordDetails() {
        if (wordId != null) {
            viewModelScope.launch {
                try {
                    val word = wordRepository.getWordById(wordId)
                    if (word != null) {
                        _uiState.update {
                            it.copy(
                                word = word.word,
                                pronunciation = word.pronunciation,
                                meaning = word.meaning,
                                description = word.description,
                                example = word.example,
                                collocation = word.collocation,
                                relatedWords = word.relatedWords,
                                note = word.note,
                                level = word.level
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(wordError = "Lỗi khi tải thông tin từ: ${e.localizedMessage}") }
                }
            }
        }
    }

    fun onWordChange(word: String) {
        _uiState.update {
            it.copy(
                word = word,
                wordError = if (word.isBlank()) "Từ vựng không được để trống" else null,
                searchError = null,
                searchSuccessMsg = null
            )
        }
    }

    fun onPronunciationChange(pronunciation: String) {
        _uiState.update { it.copy(pronunciation = pronunciation) }
    }

    fun onMeaningChange(meaning: String) {
        _uiState.update {
            it.copy(
                meaning = meaning,
                meaningError = if (meaning.isBlank()) "Nghĩa của từ không được để trống" else null
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onExampleChange(example: String) {
        _uiState.update { it.copy(example = example) }
    }

    fun onCollocationChange(collocation: String) {
        _uiState.update { it.copy(collocation = collocation) }
    }

    fun onRelatedWordsChange(relatedWords: String) {
        _uiState.update { it.copy(relatedWords = relatedWords) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onLevelChange(level: String) {
        _uiState.update { it.copy(level = level) }
    }

    fun lookupWordInDictionary() {
        val wordText = _uiState.value.word.trim()
        if (wordText.isBlank()) {
            _uiState.update { it.copy(wordError = "Vui lòng nhập từ để tra cứu") }
            return
        }

        _uiState.update { it.copy(isSearching = true, searchError = null, searchSuccessMsg = null) }

        viewModelScope.launch {
            dictionaryRepository.lookupWord(wordText)
                .onSuccess { resultWord ->
                    _uiState.update {
                        it.copy(
                            pronunciation = resultWord.pronunciation,
                            meaning = resultWord.meaning,
                            description = resultWord.description,
                            example = resultWord.example,
                            collocation = resultWord.collocation,
                            relatedWords = resultWord.relatedWords,
                            note = resultWord.note,
                            level = resultWord.level,
                            isSearching = false,
                            searchSuccessMsg = "Đã tự động điền nghĩa và ví dụ từ từ điển."
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchError = "Không tìm thấy định nghĩa hoặc có lỗi xảy ra."
                        )
                    }
                }
        }
    }

    fun onSaveWord(onSuccess: () -> Unit) {
        val state = _uiState.value
        var hasError = false
        var wordErr: String? = null
        var meaningErr: String? = null

        if (state.word.isBlank()) {
            wordErr = "Từ vựng không được để trống"
            hasError = true
        }
        if (state.meaning.isBlank()) {
            meaningErr = "Nghĩa của từ không được để trống"
            hasError = true
        }

        if (hasError) {
            _uiState.update { it.copy(wordError = wordErr, meaningError = meaningErr) }
            return
        }

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                
                if (wordId == null) {
                    val targetDeckId = deckId ?: 0
                    val newWord = Word(
                        deckId = targetDeckId,
                        word = state.word.trim(),
                        pronunciation = state.pronunciation.trim(),
                        meaning = state.meaning.trim(),
                        description = state.description.trim(),
                        example = state.example.trim(),
                        collocation = state.collocation.trim(),
                        relatedWords = state.relatedWords.trim(),
                        note = state.note.trim(),
                        level = state.level,
                        nextReviewAt = now, // Due immediately for learning
                        createdAt = now,
                        updatedAt = now
                    )
                    wordRepository.insertWord(newWord)
                } else {
                    val original = wordRepository.getWordById(wordId)
                    if (original != null) {
                        val updatedWord = original.copy(
                            word = state.word.trim(),
                            pronunciation = state.pronunciation.trim(),
                            meaning = state.meaning.trim(),
                            description = state.description.trim(),
                            example = state.example.trim(),
                            collocation = state.collocation.trim(),
                            relatedWords = state.relatedWords.trim(),
                            note = state.note.trim(),
                            level = state.level,
                            updatedAt = now
                        )
                        wordRepository.updateWord(updatedWord)
                    }
                }
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        wordError = "Lỗi khi lưu từ vựng: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(deckId: Int?, wordId: Int?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication)
                AddEditWordViewModel(
                    wordRepository = application.container.wordRepository,
                    dictionaryRepository = application.container.dictionaryRepository,
                    wordId = wordId,
                    deckId = deckId
                )
            }
        }
    }
}
