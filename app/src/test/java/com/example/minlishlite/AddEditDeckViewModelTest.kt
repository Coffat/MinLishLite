package com.example.minlishlite

import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.domain.repository.DeckRepository
import com.example.minlishlite.presentation.deck.AddEditDeckViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditDeckViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeDeckRepository: FakeDeckRepository
    private lateinit var viewModel: AddEditDeckViewModel

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
    fun loadDeck_preFillsStateWhenEditing() = runTest {
        // Arrange
        val existingDeck = Deck(id = 5, name = "TOEIC Essential", description = "For exam", tag = "TOEIC", createdAt = 100L, updatedAt = 200L)
        fakeDeckRepository.setDecks(listOf(existingDeck))

        // Act
        viewModel = AddEditDeckViewModel(fakeDeckRepository, deckId = 5)

        // Assert
        val state = viewModel.uiState.value
        assertEquals("TOEIC Essential", state.name)
        assertEquals("For exam", state.description)
        assertEquals("TOEIC", state.tag)
    }

    @Test
    fun saveDeck_whenNameBlank_setsNameErrorAndDoesNotSave() = runTest {
        // Arrange
        viewModel = AddEditDeckViewModel(fakeDeckRepository, deckId = null)
        var successCalled = false

        // Act
        viewModel.onNameChange("   ")
        viewModel.onSaveDeck {
            successCalled = false
        }

        // Assert
        val state = viewModel.uiState.value
        assertNotNull(state.nameError)
        assertEquals("Tên bộ từ không được để trống", state.nameError)
        assertTrue(fakeDeckRepository.observeAllDecks().first().isEmpty())
    }

    @Test
    fun saveDeck_whenAdding_insertsNewDeckInRepository() = runTest {
        // Arrange
        viewModel = AddEditDeckViewModel(fakeDeckRepository, deckId = null)
        var successCalled = false

        // Act
        viewModel.onNameChange("TOEIC Vocab")
        viewModel.onDescriptionChange("TOEIC test prep")
        viewModel.onTagChange("TOEIC")
        viewModel.onSaveDeck {
            successCalled = true
        }

        // Assert
        assertTrue(successCalled)
        val decks = fakeDeckRepository.observeAllDecks().first()
        assertEquals(1, decks.size)
        val savedDeck = decks.first()
        assertEquals("TOEIC Vocab", savedDeck.name)
        assertEquals("TOEIC test prep", savedDeck.description)
        assertEquals("TOEIC", savedDeck.tag)
    }

    @Test
    fun saveDeck_whenEditing_updatesDeckAndPreservesCreatedAt() = runTest {
        // Arrange
        val originalDeck = Deck(id = 5, name = "Original", description = "Desc", tag = "TAG", createdAt = 555L, updatedAt = 666L)
        fakeDeckRepository.setDecks(listOf(originalDeck))
        viewModel = AddEditDeckViewModel(fakeDeckRepository, deckId = 5)
        var successCalled = false

        // Act
        viewModel.onNameChange("Updated Name")
        viewModel.onDescriptionChange("Updated Desc")
        viewModel.onSaveDeck {
            successCalled = true
        }

        // Assert
        assertTrue(successCalled)
        val decks = fakeDeckRepository.observeAllDecks().first()
        assertEquals(1, decks.size)
        val savedDeck = decks.first()
        assertEquals(5, savedDeck.id)
        assertEquals("Updated Name", savedDeck.name)
        assertEquals("Updated Desc", savedDeck.description)
        // Verify createdAt is preserved
        assertEquals(555L, savedDeck.createdAt)
    }

    @Test
    fun deleteDeck_invokesRepositoryDeleteAndNavigates() = runTest {
        // Arrange
        val existingDeck = Deck(id = 5, name = "TOEIC", description = "Prep", tag = "TOEIC", createdAt = 0L, updatedAt = 0L)
        fakeDeckRepository.setDecks(listOf(existingDeck))
        viewModel = AddEditDeckViewModel(fakeDeckRepository, deckId = 5)
        var successCalled = false

        // Act
        viewModel.onDeleteDeck {
            successCalled = true
        }

        // Assert
        assertTrue(successCalled)
        assertTrue(fakeDeckRepository.observeAllDecks().first().isEmpty())
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
