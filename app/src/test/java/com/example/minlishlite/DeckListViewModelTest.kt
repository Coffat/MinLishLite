package com.example.minlishlite

import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.domain.repository.DeckRepository
import com.example.minlishlite.presentation.deck.DeckListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeckListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeDeckRepository: FakeDeckRepository
    private lateinit var viewModel: DeckListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDeckRepository = FakeDeckRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsAllDecksAndExtractsUniqueSortedTags() = runTest {
        // Arrange
        val decks = listOf(
            Deck(id = 1, name = "TOEIC", description = "Test Prep", tag = "TOEIC", createdAt = 0L, updatedAt = 0L),
            Deck(id = 2, name = "IELTS Academic", description = "For academic study", tag = "IELTS", createdAt = 0L, updatedAt = 0L),
            Deck(id = 3, name = "Travel English", description = "Everyday phrases", tag = "TRAVEL", createdAt = 0L, updatedAt = 0L),
            Deck(id = 4, name = "IELTS General", description = "General training", tag = "IELTS", createdAt = 0L, updatedAt = 0L),
            Deck(id = 5, name = "Basic Wordlist", description = "Simple words", tag = "", createdAt = 0L, updatedAt = 0L)
        )
        fakeDeckRepository.setDecks(decks)

        // Act
        viewModel = DeckListViewModel(fakeDeckRepository)
        val collectJob = launch { viewModel.uiState.collect {} }
        runCurrent()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(5, state.decks.size)
        assertEquals(listOf("IELTS", "TOEIC", "TRAVEL"), state.allTags)
        
        collectJob.cancel()
    }

    @Test
    fun searchQueryChange_filtersDecksByNameOrDescription() = runTest {
        // Arrange
        val decks = listOf(
            Deck(id = 1, name = "TOEIC Essential", description = "Prep", tag = "TOEIC", createdAt = 0L, updatedAt = 0L),
            Deck(id = 2, name = "IELTS Academic", description = "IELTS vocab prep", tag = "IELTS", createdAt = 0L, updatedAt = 0L),
            Deck(id = 3, name = "Travel phrases", description = "Everyday conversations", tag = "TRAVEL", createdAt = 0L, updatedAt = 0L)
        )
        fakeDeckRepository.setDecks(decks)
        viewModel = DeckListViewModel(fakeDeckRepository)
        val collectJob = launch { viewModel.uiState.collect {} }
        runCurrent()

        // Act & Assert 1: Search by name
        viewModel.onSearchQueryChange("TOEIC")
        runCurrent()
        assertEquals(1, viewModel.uiState.value.decks.size)
        assertEquals("TOEIC Essential", viewModel.uiState.value.decks.first().name)

        // Act & Assert 2: Search by description
        viewModel.onSearchQueryChange("vocab")
        runCurrent()
        assertEquals(1, viewModel.uiState.value.decks.size)
        assertEquals("IELTS Academic", viewModel.uiState.value.decks.first().name)

        // Act & Assert 3: Search with no results
        viewModel.onSearchQueryChange("random")
        runCurrent()
        assertTrue(viewModel.uiState.value.decks.isEmpty())
        
        collectJob.cancel()
    }

    @Test
    fun tagSelect_filtersDecksByTag() = runTest {
        // Arrange
        val decks = listOf(
            Deck(id = 1, name = "TOEIC", description = "Prep", tag = "TOEIC", createdAt = 0L, updatedAt = 0L),
            Deck(id = 2, name = "IELTS Academic", description = "IELTS vocab prep", tag = "IELTS", createdAt = 0L, updatedAt = 0L),
            Deck(id = 3, name = "IELTS General", description = "Simple IELTS", tag = "IELTS", createdAt = 0L, updatedAt = 0L)
        )
        fakeDeckRepository.setDecks(decks)
        viewModel = DeckListViewModel(fakeDeckRepository)
        val collectJob = launch { viewModel.uiState.collect {} }
        runCurrent()

        // Act
        viewModel.onTagSelect("IELTS")
        runCurrent()

        // Assert
        assertEquals(2, viewModel.uiState.value.decks.size)
        assertTrue(viewModel.uiState.value.decks.all { it.tag == "IELTS" })

        // Act to reset (tag select null)
        viewModel.onTagSelect(null)
        runCurrent()
        assertEquals(3, viewModel.uiState.value.decks.size)
        
        collectJob.cancel()
    }

    @Test
    fun deleteDeck_invokesRepositoryDelete() = runTest {
        // Arrange
        val decks = listOf(
            Deck(id = 1, name = "TOEIC", description = "Prep", tag = "TOEIC", createdAt = 0L, updatedAt = 0L)
        )
        fakeDeckRepository.setDecks(decks)
        viewModel = DeckListViewModel(fakeDeckRepository)
        val collectJob = launch { viewModel.uiState.collect {} }
        runCurrent()

        // Act
        viewModel.onDeleteDeck(1)
        runCurrent()

        // Assert
        assertTrue(fakeDeckRepository.observeAllDecks().first().isEmpty())
        
        collectJob.cancel()
    }

    // Fake DeckRepository Implementation
    private class FakeDeckRepository : DeckRepository {
        private val decksFlow = MutableStateFlow<List<Deck>>(emptyList())

        override fun observeAllDecks(): Flow<List<Deck>> = decksFlow

        override suspend fun getDeckById(id: Int): Deck? {
            return decksFlow.value.find { it.id == id }
        }

        override suspend fun insertDeck(deck: Deck): Long {
            val list = decksFlow.value.toMutableList()
            val newId = (list.maxOfOrNull { it.id } ?: 0) + 1
            list.add(deck.copy(id = newId))
            decksFlow.value = list
            return newId.toLong()
        }

        override suspend fun updateDeck(deck: Deck) {
            val list = decksFlow.value.toMutableList()
            val index = list.indexOfFirst { it.id == deck.id }
            if (index != -1) {
                list[index] = deck
                decksFlow.value = list
            }
        }

        override suspend fun deleteDeckById(id: Int) {
            val list = decksFlow.value.toMutableList()
            list.removeAll { it.id == id }
            decksFlow.value = list
        }

        fun setDecks(decks: List<Deck>) {
            decksFlow.value = decks
        }
    }
}
