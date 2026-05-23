package com.example.minlishlite.presentation.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.domain.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DeckListUiState(
    val decks: List<Deck> = emptyList(),
    val allTags: List<String> = emptyList(),
    val searchQuery: String = "",
    val selectedTag: String? = null,
    val isLoading: Boolean = false
)

class DeckListViewModel(
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedTag = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<DeckListUiState> = combine(
        deckRepository.observeAllDecks(),
        _searchQuery,
        _selectedTag,
        _isLoading
    ) { allDecks, query, tag, loading ->
        val tags = allDecks.map { it.tag.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()

        val filteredDecks = allDecks.filter { deck ->
            val matchesSearch = deck.name.contains(query, ignoreCase = true) ||
                    deck.description.contains(query, ignoreCase = true)
            val matchesTag = tag == null || deck.tag.equals(tag, ignoreCase = true)
            matchesSearch && matchesTag
        }

        DeckListUiState(
            decks = filteredDecks,
            allTags = tags,
            searchQuery = query,
            selectedTag = tag,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeckListUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTagSelect(tag: String?) {
        _selectedTag.value = tag
    }

    fun onDeleteDeck(deckId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            deckRepository.deleteDeckById(deckId)
            _isLoading.value = false
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication)
                DeckListViewModel(application.container.deckRepository)
            }
        }
    }
}
