package com.example.minlishlite.presentation.review

import app.cash.turbine.test
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.data.repository.DeckRepository
import com.example.minlishlite.data.repository.WordRepository
import com.example.minlishlite.presentation.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReviewTodayViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var wordRepository: WordRepository
    private lateinit var deckRepository: DeckRepository
    private lateinit var viewModel: ReviewTodayViewModel

    @Before
    fun setup() {
        wordRepository = mockk()
        deckRepository = mockk()
    }

    @Test
    fun uiState_emitsLoadingThenData() = runTest(UnconfinedTestDispatcher()) {
        val decksFlow = MutableStateFlow<List<DeckEntity>>(emptyList())
        val dueWordsFlow = MutableStateFlow<List<WordEntity>>(emptyList())

        every { deckRepository.observeAllDecks() } returns decksFlow
        every { wordRepository.observeWordsDueToday(any()) } returns dueWordsFlow

        viewModel = ReviewTodayViewModel(wordRepository, deckRepository)

        viewModel.uiState.test {
            val initialState = awaitItem()
            
            decksFlow.value = listOf(DeckEntity(id = 1, name = "Deck 1", description = "", tag = "", createdAt = 0, updatedAt = 0))
            dueWordsFlow.value = listOf(
                WordEntity(
                    id = 1,
                    deckId = 1,
                    word = "apple",
                    pronunciation = "",
                    pronunciationUk = "",
                    pronunciationUs = "",
                    pronunciationAudioUrl = "",
                    pronunciationUkAudioUrl = "",
                    pronunciationUsAudioUrl = "",
                    meaning = "quả táo",
                    description = "",
                    example = "",
                    collocation = "",
                    relatedWords = "",
                    note = "",
                    level = "Beginner",
                    easeFactor = 2.5f,
                    nextReviewAt = 0,
                    createdAt = 0,
                    updatedAt = 0
                )
            )

            val updatedState = awaitItem()
            assertFalse(updatedState.isLoading)
            assertEquals(1, updatedState.dueCount)
            assertEquals("Deck 1", updatedState.dueWords[0].deckName)
            assertEquals("apple", updatedState.dueWords[0].word)
            assertEquals("quả táo", updatedState.dueWords[0].meaning)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
