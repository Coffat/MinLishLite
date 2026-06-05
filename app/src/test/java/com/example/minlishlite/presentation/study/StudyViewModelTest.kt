package com.example.minlishlite.presentation.study

import app.cash.turbine.test
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.data.repository.DeckRepository
import com.example.minlishlite.data.repository.StudyRepository
import com.example.minlishlite.data.repository.WordRepository
import com.example.minlishlite.data.model.ReviewResult
import com.example.minlishlite.presentation.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StudyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var deckRepository: DeckRepository
    private lateinit var wordRepository: WordRepository
    private lateinit var studyRepository: StudyRepository
    private lateinit var viewModel: StudyViewModel

    @Before
    fun setup() {
        deckRepository = mockk()
        wordRepository = mockk()
        studyRepository = mockk()
    }

    private fun createWordEntity(id: Int, nextReviewAt: Long): WordEntity {
        return WordEntity(
            id = id,
            deckId = 1,
            word = "word$id",
            pronunciation = "",
            pronunciationUk = "",
            pronunciationUs = "",
            pronunciationAudioUrl = "",
            pronunciationUkAudioUrl = "",
            pronunciationUsAudioUrl = "",
            meaning = "meaning$id",
            description = "",
            example = "",
            collocation = "",
            relatedWords = "",
            note = "",
            level = "Beginner",
            easeFactor = 2.5f,
            nextReviewAt = nextReviewAt,
            createdAt = 0,
            updatedAt = 0
        )
    }

    @Test
    fun init_loadsDeckAndWords_setsUiStateCorrectly() = runTest {
        val wordsFlow = MutableStateFlow<List<WordEntity>>(listOf(
                createWordEntity(1, 0),
                createWordEntity(2, 0)
            ))
        coEvery { deckRepository.getDeckById(1) } returns DeckEntity(1, "My Deck", "", "", 0, 0)
        every { wordRepository.observeWordsByDeckId(1) } returns wordsFlow

        viewModel = StudyViewModel(deckRepository, wordRepository, studyRepository, StudyMode.DeckDue(1))

        viewModel.uiState.test {
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals("My Deck", loadedState.deckName)
            assertEquals(2, loadedState.totalCount)
            assertEquals(0, loadedState.currentIndex)
            assertEquals("1/2", loadedState.progressLabel)
            assertEquals(false, loadedState.isFlipped)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFlipCard_flipsCardIfNotComplete() = runTest {
        val wordsFlow = MutableStateFlow<List<WordEntity>>(listOf(createWordEntity(1, 0)))
        coEvery { deckRepository.getDeckById(1) } returns DeckEntity(1, "My Deck", "", "", 0, 0)
        every { wordRepository.observeWordsByDeckId(1) } returns wordsFlow

        viewModel = StudyViewModel(deckRepository, wordRepository, studyRepository, StudyMode.DeckDue(1))

        viewModel.uiState.test {
            val s1 = awaitItem()
            
            viewModel.onFlipCard()
            val s2 = awaitItem()
            assertTrue(s2.isFlipped)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onRateCard_updatesProgressAndFlipsBack() = runTest {
        val wordsFlow = MutableStateFlow<List<WordEntity>>(listOf(createWordEntity(1, 0), createWordEntity(2, 0)))
        coEvery { deckRepository.getDeckById(1) } returns DeckEntity(1, "My Deck", "", "", 0, 0)
        every { wordRepository.observeWordsByDeckId(1) } returns wordsFlow
        coEvery { studyRepository.reviewWord(any(), any(), any(), any()) } returns Unit

        viewModel = StudyViewModel(deckRepository, wordRepository, studyRepository, StudyMode.DeckDue(1))

        viewModel.uiState.test {
            val state1 = awaitItem()
            assertEquals(0, state1.currentIndex)
            assertFalse(state1.isFlipped)

            viewModel.onFlipCard()
            val state2 = awaitItem()
            assertTrue(state2.isFlipped)

            viewModel.onRateCard(ReviewResult.GOOD)

            val state4 = awaitItem()
            assertFalse(state4.isSubmittingRating)
            assertEquals(1, state4.currentIndex)
            assertFalse(state4.isFlipped)
            assertEquals("2/2", state4.progressLabel)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
