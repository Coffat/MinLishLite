package com.example.minlishlite.presentation.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.core.result.NetworkException
import com.example.minlishlite.core.result.WordNotFoundException
import com.example.minlishlite.data.mapper.buildCombinedPronunciation
import com.example.minlishlite.data.mapper.resolvePronunciationFields
import com.example.minlishlite.data.mapper.sanitizePhoneticInput
import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.domain.repository.DictionaryRepository
import com.example.minlishlite.domain.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DictionaryLookupPreview(
    val pronunciationUk: String,
    val pronunciationUs: String,
    val pronunciationUkAudioUrl: String = "",
    val pronunciationUsAudioUrl: String = "",
    val meaning: String,
    val description: String,
    val example: String,
    val collocation: String,
    val relatedWords: String,
    val note: String,
)

data class AddEditWordUiState(
    val word: String = "",
    val pronunciationUk: String = "",
    val pronunciationUs: String = "",
    val pronunciationUkAudioUrl: String = "",
    val pronunciationUsAudioUrl: String = "",
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
    val searchSuccessMsg: String? = null,
    val lookupPreview: DictionaryLookupPreview? = null
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
                        val pronunciations = resolvePronunciationFields(
                            pronunciation = word.pronunciation,
                            pronunciationUk = word.pronunciationUk,
                            pronunciationUs = word.pronunciationUs,
                            pronunciationUkAudioUrl = word.pronunciationUkAudioUrl,
                            pronunciationUsAudioUrl = word.pronunciationUsAudioUrl,
                            pronunciationAudioUrl = word.pronunciationAudioUrl
                        )
                        _uiState.update {
                            it.copy(
                                word = word.word,
                                pronunciationUk = pronunciations.ukText,
                                pronunciationUs = pronunciations.usText,
                                pronunciationUkAudioUrl = pronunciations.ukAudioUrl,
                                pronunciationUsAudioUrl = pronunciations.usAudioUrl,
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
                searchSuccessMsg = null,
                lookupPreview = null
            )
        }
    }

    fun onPronunciationUkChange(pronunciationUk: String) {
        _uiState.update { it.copy(pronunciationUk = sanitizePhoneticInput(pronunciationUk)) }
    }

    fun onPronunciationUsChange(pronunciationUs: String) {
        _uiState.update { it.copy(pronunciationUs = sanitizePhoneticInput(pronunciationUs)) }
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

        _uiState.update {
            it.copy(
                isSearching = true,
                searchError = null,
                searchSuccessMsg = null,
                lookupPreview = null
            )
        }

        viewModelScope.launch {
            dictionaryRepository.lookupWord(wordText)
                .onSuccess { resultWord ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            lookupPreview = resultWord.toLookupPreview(),
                            searchSuccessMsg = "Đã tìm thấy kết quả. Xem preview và áp dụng vào form."
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchError = mapLookupError(error)
                        )
                    }
                }
        }
    }

    fun applyLookupPreview() {
        val preview = _uiState.value.lookupPreview ?: return

        _uiState.update {
            it.copy(
                pronunciationUk = sanitizePhoneticInput(preview.pronunciationUk),
                pronunciationUs = sanitizePhoneticInput(preview.pronunciationUs),
                pronunciationUkAudioUrl = preview.pronunciationUkAudioUrl,
                pronunciationUsAudioUrl = preview.pronunciationUsAudioUrl,
                meaning = preview.meaning,
                description = preview.description,
                example = preview.example,
                collocation = preview.collocation,
                relatedWords = preview.relatedWords,
                note = preview.note,
                meaningError = if (preview.meaning.isBlank()) {
                    "Nghĩa của từ không được để trống"
                } else {
                    null
                },
                lookupPreview = null,
                searchSuccessMsg = "Đã áp dụng kết quả tra cứu vào form."
            )
        }
    }

    fun dismissLookupPreview() {
        _uiState.update {
            it.copy(
                lookupPreview = null,
                searchSuccessMsg = null
            )
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
                val trimmedWord = state.word.trim()
                val targetDeckId = deckId ?: wordRepository.getWordById(wordId ?: -1)?.deckId
                if (targetDeckId != null) {
                    val duplicateExists = wordRepository.observeWordsByDeckId(targetDeckId).first()
                        .any { existing ->
                            existing.id != wordId &&
                                existing.word.equals(trimmedWord, ignoreCase = true)
                        }
                    if (duplicateExists) {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                wordError = "Từ này đã tồn tại trong bộ từ"
                            )
                        }
                        return@launch
                    }
                }

                val now = System.currentTimeMillis()
                val combinedPronunciation = buildCombinedPronunciation(
                    state.pronunciationUk.trim(),
                    state.pronunciationUs.trim()
                )

                if (wordId == null) {
                    val resolvedDeckId = deckId ?: 0
                    val newWord = Word(
                        deckId = resolvedDeckId,
                        word = trimmedWord,
                        pronunciation = combinedPronunciation,
                        pronunciationUk = state.pronunciationUk.trim(),
                        pronunciationUs = state.pronunciationUs.trim(),
                        pronunciationUkAudioUrl = state.pronunciationUkAudioUrl.trim(),
                        pronunciationUsAudioUrl = state.pronunciationUsAudioUrl.trim(),
                        pronunciationAudioUrl = state.pronunciationUkAudioUrl.trim(),
                        meaning = state.meaning.trim(),
                        description = state.description.trim(),
                        example = state.example.trim(),
                        collocation = state.collocation.trim(),
                        relatedWords = state.relatedWords.trim(),
                        note = state.note.trim(),
                        level = state.level,
                        nextReviewAt = now,
                        createdAt = now,
                        updatedAt = now
                    )
                    wordRepository.insertWord(newWord)
                } else {
                    val original = wordRepository.getWordById(wordId)
                    if (original != null) {
                        val updatedWord = original.copy(
                            word = trimmedWord,
                            pronunciation = combinedPronunciation,
                            pronunciationUk = state.pronunciationUk.trim(),
                            pronunciationUs = state.pronunciationUs.trim(),
                            pronunciationUkAudioUrl = state.pronunciationUkAudioUrl.trim(),
                            pronunciationUsAudioUrl = state.pronunciationUsAudioUrl.trim(),
                            pronunciationAudioUrl = state.pronunciationUkAudioUrl.trim(),
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

    private fun mapLookupError(error: Throwable): String {
        return when (error) {
            is WordNotFoundException -> "Không tìm thấy từ trong từ điển."
            is NetworkException -> "Không có kết nối mạng. Vui lòng thử lại."
            else -> "Không tra được từ. Vui lòng thử lại."
        }
    }

    private fun Word.toLookupPreview(): DictionaryLookupPreview {
        val pronunciations = resolvePronunciationFields(
            pronunciation = pronunciation,
            pronunciationUk = pronunciationUk,
            pronunciationUs = pronunciationUs,
            pronunciationUkAudioUrl = pronunciationUkAudioUrl,
            pronunciationUsAudioUrl = pronunciationUsAudioUrl,
            pronunciationAudioUrl = pronunciationAudioUrl
        )

        return DictionaryLookupPreview(
            pronunciationUk = pronunciations.ukText,
            pronunciationUs = pronunciations.usText,
            pronunciationUkAudioUrl = pronunciations.ukAudioUrl,
            pronunciationUsAudioUrl = pronunciations.usAudioUrl,
            meaning = meaning,
            description = description,
            example = example,
            collocation = collocation,
            relatedWords = relatedWords,
            note = note
        )
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
