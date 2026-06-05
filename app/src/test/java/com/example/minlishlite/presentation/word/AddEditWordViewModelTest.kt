package com.example.minlishlite.presentation.word

import app.cash.turbine.test
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.data.model.DictionaryResult
import com.example.minlishlite.data.repository.DictionaryRepository
import com.example.minlishlite.data.repository.WordRepository
import com.example.minlishlite.presentation.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditWordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var wordRepository: WordRepository
    private lateinit var dictionaryRepository: DictionaryRepository
    private lateinit var viewModel: AddEditWordViewModel

    @Before
    fun setup() {
        wordRepository = mockk()
        dictionaryRepository = mockk()
    }

    private fun createWordEntity(id: Int, word: String): WordEntity {
        return WordEntity(
            id = id,
            deckId = 1,
            word = word,
            pronunciation = "",
            pronunciationUk = "",
            pronunciationUs = "",
            pronunciationAudioUrl = "",
            pronunciationUkAudioUrl = "",
            pronunciationUsAudioUrl = "",
            meaning = "meaning",
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
    }

    @Test
    fun init_withWordId_loadsWordData() = runTest {
        coEvery { wordRepository.getWordById(1) } returns createWordEntity(1, "apple")

        viewModel = AddEditWordViewModel(wordRepository, dictionaryRepository, wordId = 1, deckId = null)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("apple", state.word)
            assertEquals("meaning", state.meaning)
        }
    }

    @Test
    fun onWordChange_updatesWordAndClearsErrors() = runTest {
        viewModel = AddEditWordViewModel(wordRepository, dictionaryRepository, null, 1)

        viewModel.uiState.test {
            awaitItem()
            
            viewModel.onWordChange("banana")
            val state = awaitItem()
            
            assertEquals("banana", state.word)
            assertNull(state.wordError)
            assertNull(state.searchError)
            assertNull(state.searchSuccessMsg)
        }
    }

    @Test
    fun lookupWordInDictionary_success_showsPreview() = runTest {
        viewModel = AddEditWordViewModel(wordRepository, dictionaryRepository, null, 1)
        
        val dictResult = DictionaryResult(
            word = "apple",
            pronunciationUk = "/æp.əl/",
            pronunciationUs = "/æp.əl/",
            pronunciationUkAudioUrl = "",
            pronunciationUsAudioUrl = "",
            meaning = "quả táo",
            definition = "a fruit",
            partOfSpeech = "noun",
            example = "I eat an apple"
        )
        coEvery { dictionaryRepository.lookupWord("apple") } returns Result.success(dictResult)

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.onWordChange("apple")
            awaitItem() // word changed

            viewModel.lookupWordInDictionary()
            
            val loadingState = awaitItem()
            assertTrue(loadingState.isSearching)

            val successState = awaitItem()
            assertFalse(successState.isSearching)
            assertNotNull(successState.lookupPreview)
            assertEquals("quả táo", successState.lookupPreview?.meaning)
            assertEquals("a fruit", successState.lookupPreview?.description)
        }
    }

    @Test
    fun onSaveWord_emptyFields_showsErrors() = runTest {
        viewModel = AddEditWordViewModel(wordRepository, dictionaryRepository, null, 1)

        viewModel.uiState.test {
            awaitItem() // initial state has empty word/meaning
            
            var saved = false
            viewModel.onSaveWord { saved = true }
            
            val errorState = awaitItem()
            assertEquals("Từ vựng không được để trống", errorState.wordError)
            assertEquals("Nghĩa của từ không được để trống", errorState.meaningError)
            assertFalse(saved)
        }
    }

    @Test
    fun onSaveWord_duplicateWord_showsError() = runTest {
        viewModel = AddEditWordViewModel(wordRepository, dictionaryRepository, null, 1)
        val wordsFlow = MutableStateFlow(listOf(createWordEntity(2, "apple")))
        every { wordRepository.observeWordsByDeckId(1) } returns wordsFlow

        viewModel.onWordChange("apple")
        viewModel.onMeaningChange("quả táo")

        var saved = false
        viewModel.onSaveWord { saved = true }

        val duplicateState = viewModel.uiState.value
        assertFalse(duplicateState.isSaving)
        assertEquals("Từ này đã tồn tại trong bộ từ", duplicateState.wordError)
        assertFalse(saved)
    }

    @Test
    fun onSaveWord_validData_savesAndCallsCallback() = runTest {
        viewModel = AddEditWordViewModel(wordRepository, dictionaryRepository, null, 1)
        val wordsFlow = MutableStateFlow<List<WordEntity>>(emptyList())
        every { wordRepository.observeWordsByDeckId(1) } returns wordsFlow
        coEvery { wordRepository.insertWord(any()) } returns 1

        viewModel.onWordChange("apple")
        viewModel.onMeaningChange("quả táo")

        var saved = false
        viewModel.onSaveWord { saved = true }

        val completeState = viewModel.uiState.value
        assertFalse(completeState.isSaving)
        assertTrue(saved)
    }
}
