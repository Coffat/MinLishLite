package com.example.minlishlite.presentation.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditDeckUiState(
    val name: String = "",
    val description: String = "",
    val tag: String = "",
    val isSaving: Boolean = false,
    val nameError: String? = null
)

class AddEditDeckViewModel(
    private val deckRepository: DeckRepository,
    private val deckId: Int?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditDeckUiState())
    val uiState: StateFlow<AddEditDeckUiState> = _uiState.asStateFlow()

    init {
        loadDeck()
    }

    private fun loadDeck() {
        if (deckId != null) {
            viewModelScope.launch {
                val deck = deckRepository.getDeckById(deckId)
                if (deck != null) {
                    _uiState.update {
                        it.copy(
                            name = deck.name,
                            description = deck.description,
                            tag = deck.tag
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(
                name = name,
                nameError = if (name.isBlank()) "Tên bộ từ không được để trống" else null
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onTagChange(tag: String) {
        _uiState.update { it.copy(tag = tag) }
    }

    fun onSaveDeck(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Tên bộ từ không được để trống") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val trimmedName = state.name.trim()
            val duplicateExists = deckRepository.observeAllDecks().first().any { existing ->
                existing.id != deckId && existing.name.equals(trimmedName, ignoreCase = true)
            }
            if (duplicateExists) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        nameError = "Đã tồn tại bộ từ với tên này"
                    )
                }
                return@launch
            }

            val now = System.currentTimeMillis()
            val deck = DeckEntity(
                id = deckId ?: 0,
                name = trimmedName,
                description = state.description.trim(),
                tag = state.tag.trim(),
                createdAt = now,
                updatedAt = now
            )

            if (deckId == null) {
                deckRepository.insertDeck(deck)
            } else {
                val original = deckRepository.getDeckById(deckId)
                val updated = deck.copy(
                    createdAt = original?.createdAt ?: now
                )
                deckRepository.updateDeck(updated)
            }
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }

    fun onDeleteDeck(onSuccess: () -> Unit) {
        if (deckId == null) return
        viewModelScope.launch {
            deckRepository.deleteDeckById(deckId)
            onSuccess()
        }
    }

    companion object {
        fun provideFactory(deckId: Int?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication)
                AddEditDeckViewModel(application.container.deckRepository, deckId)
            }
        }
    }
}
