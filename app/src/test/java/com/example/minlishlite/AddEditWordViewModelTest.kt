package com.example.minlishlite

import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.domain.repository.DictionaryRepository
import com.example.minlishlite.domain.repository.WordRepository
import com.example.minlishlite.presentation.word.AddEditWordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class AddEditWordViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeWordRepository: FakeWordRepository
    private lateinit var fakeDictionaryRepository: FakeDictionaryRepository
    private lateinit var viewModel: AddEditWordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeWordRepository = FakeWordRepository()
        fakeDictionaryRepository = FakeDictionaryRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadWord_preFillsStateWhenEditing() = runTest {
        // Arrange
        val existingWord = Word(
            id = 10,
            deckId = 2,
            word = "apple",
            pronunciation = "/ˈæp.əl/",
            meaning = "Quả táo",
            description = "A round fruit with red or green skin",
            example = "I ate a red apple.",
            collocation = "eat an apple",
            relatedWords = "fruit",
            note = "My favorite fruit",
            level = "Beginner",
            nextReviewAt = 500L,
            createdAt = 100L,
            updatedAt = 200L
        )
        fakeWordRepository.setWords(listOf(existingWord))

        // Act
        viewModel = AddEditWordViewModel(
            wordRepository = fakeWordRepository,
            dictionaryRepository = fakeDictionaryRepository,
            wordId = 10,
            deckId = null
        )

        // Assert
        val state = viewModel.uiState.value
        assertEquals("apple", state.word)
        assertEquals("/ˈæp.əl/", state.pronunciation)
        assertEquals("Quả táo", state.meaning)
        assertEquals("A round fruit with red or green skin", state.description)
        assertEquals("I ate a red apple.", state.example)
        assertEquals("eat an apple", state.collocation)
        assertEquals("fruit", state.relatedWords)
        assertEquals("My favorite fruit", state.note)
        assertEquals("Beginner", state.level)
    }

    @Test
    fun saveWord_whenWordOrMeaningBlank_setsErrorsAndDoesNotSave() = runTest {
        // Arrange
        viewModel = AddEditWordViewModel(
            wordRepository = fakeWordRepository,
            dictionaryRepository = fakeDictionaryRepository,
            wordId = null,
            deckId = 1
        )
        var successCalled = false

        // Act
        viewModel.onWordChange("   ")
        viewModel.onMeaningChange("")
        viewModel.onSaveWord {
            successCalled = true
        }

        // Assert
        val state = viewModel.uiState.value
        assertNotNull(state.wordError)
        assertNotNull(state.meaningError)
        assertEquals("Từ vựng không được để trống", state.wordError)
        assertEquals("Nghĩa của từ không được để trống", state.meaningError)
        assertTrue(fakeWordRepository.getWords().isEmpty())
        assertTrue(!successCalled)
    }

    @Test
    fun saveWord_whenAdding_insertsNewWordInRepository() = runTest {
        // Arrange
        viewModel = AddEditWordViewModel(
            wordRepository = fakeWordRepository,
            dictionaryRepository = fakeDictionaryRepository,
            wordId = null,
            deckId = 1
        )
        var successCalled = false

        // Act
        viewModel.onWordChange("banana")
        viewModel.onPronunciationChange("/bəˈnɑː.nə/")
        viewModel.onMeaningChange("Quả chuối")
        viewModel.onDescriptionChange("A long curved fruit")
        viewModel.onExampleChange("Bananas are yellow.")
        viewModel.onCollocationChange("ripe banana")
        viewModel.onRelatedWordsChange("yellow fruit")
        viewModel.onNoteChange("Sweet and healthy")
        viewModel.onLevelChange("Intermediate")
        
        viewModel.onSaveWord {
            successCalled = true
        }

        // Assert
        assertTrue(successCalled)
        val words = fakeWordRepository.getWords()
        assertEquals(1, words.size)
        
        val savedWord = words.first()
        assertEquals(1, savedWord.deckId)
        assertEquals("banana", savedWord.word)
        assertEquals("/bəˈnɑː.nə/", savedWord.pronunciation)
        assertEquals("Quả chuối", savedWord.meaning)
        assertEquals("A long curved fruit", savedWord.description)
        assertEquals("Bananas are yellow.", savedWord.example)
        assertEquals("ripe banana", savedWord.collocation)
        assertEquals("yellow fruit", savedWord.relatedWords)
        assertEquals("Sweet and healthy", savedWord.note)
        assertEquals("Intermediate", savedWord.level)
        assertTrue(savedWord.nextReviewAt > 0) // Should set current time for immediately review
    }

    @Test
    fun saveWord_whenEditing_updatesWordAndPreservesSRS() = runTest {
        // Arrange
        val originalWord = Word(
            id = 7,
            deckId = 1,
            word = "run",
            pronunciation = "/rʌn/",
            meaning = "chạy",
            description = "move fast on foot",
            example = "He runs fast.",
            collocation = "go running",
            relatedWords = "sprint",
            note = "verb",
            level = "Beginner",
            nextReviewAt = 9999L,
            reviewCount = 5,
            correctCount = 4,
            createdAt = 100L,
            updatedAt = 200L
        )
        fakeWordRepository.setWords(listOf(originalWord))

        viewModel = AddEditWordViewModel(
            wordRepository = fakeWordRepository,
            dictionaryRepository = fakeDictionaryRepository,
            wordId = 7,
            deckId = null
        )
        var successCalled = false

        // Act
        viewModel.onMeaningChange("chạy bộ")
        viewModel.onNoteChange("irregular verb")
        viewModel.onLevelChange("Intermediate")
        
        viewModel.onSaveWord {
            successCalled = true
        }

        // Assert
        assertTrue(successCalled)
        val words = fakeWordRepository.getWords()
        assertEquals(1, words.size)
        
        val updatedWord = words.first()
        assertEquals(7, updatedWord.id)
        assertEquals("run", updatedWord.word) // Unchanged
        assertEquals("chạy bộ", updatedWord.meaning) // Updated
        assertEquals("irregular verb", updatedWord.note) // Updated
        assertEquals("Intermediate", updatedWord.level) // Updated
        // Verify metadata preserved
        assertEquals(9999L, updatedWord.nextReviewAt)
        assertEquals(5, updatedWord.reviewCount)
        assertEquals(4, updatedWord.correctCount)
        assertEquals(100L, updatedWord.createdAt)
    }

    @Test
    fun lookupWord_whenSpelledWordExists_fillsFieldsAndClearsErrors() = runTest {
        // Arrange
        viewModel = AddEditWordViewModel(
            wordRepository = fakeWordRepository,
            dictionaryRepository = fakeDictionaryRepository,
            wordId = null,
            deckId = 1
        )
        
        val resultWord = Word(
            deckId = 0,
            word = "hello",
            pronunciation = "/həˈləʊ/",
            meaning = "Xin chào",
            description = "A greeting",
            example = "Hello there!",
            collocation = "say hello",
            relatedWords = "hi",
            note = "greeting",
            level = "Beginner",
            nextReviewAt = 0,
            createdAt = 0,
            updatedAt = 0
        )
        fakeDictionaryRepository.resultToReturn = Result.success(resultWord)

        // Act
        viewModel.onWordChange("hello")
        viewModel.lookupWordInDictionary()

        // Assert
        val state = viewModel.uiState.value
        assertEquals("/həˈləʊ/", state.pronunciation)
        assertEquals("Xin chào", state.meaning)
        assertEquals("A greeting", state.description)
        assertEquals("Hello there!", state.example)
        assertEquals("say hello", state.collocation)
        assertEquals("hi", state.relatedWords)
        assertEquals("greeting", state.note)
        assertEquals("Beginner", state.level)
        assertNull(state.searchError)
        assertNotNull(state.searchSuccessMsg)
    }

    @Test
    fun lookupWord_whenSpelledWordNotFound_showsSearchError() = runTest {
        // Arrange
        viewModel = AddEditWordViewModel(
            wordRepository = fakeWordRepository,
            dictionaryRepository = fakeDictionaryRepository,
            wordId = null,
            deckId = 1
        )
        fakeDictionaryRepository.resultToReturn = Result.failure(Exception("Not found"))

        // Act
        viewModel.onWordChange("unknownword")
        viewModel.lookupWordInDictionary()

        // Assert
        val state = viewModel.uiState.value
        assertNotNull(state.searchError)
        assertNull(state.searchSuccessMsg)
        assertEquals("Không tìm thấy định nghĩa hoặc có lỗi xảy ra.", state.searchError)
    }

    // Fake WordRepository Implementation
    private class FakeWordRepository : WordRepository {
        private val wordsFlow = MutableStateFlow<List<Word>>(emptyList())

        override fun observeWordsByDeckId(deckId: Int): Flow<List<Word>> = 
            MutableStateFlow(wordsFlow.value.filter { it.deckId == deckId })

        override fun observeWordsDueToday(currentTime: Long): Flow<List<Word>> = 
            MutableStateFlow(wordsFlow.value.filter { it.nextReviewAt <= currentTime })

        override suspend fun getWordById(id: Int): Word? {
            return wordsFlow.value.find { it.id == id }
        }

        override suspend fun insertWord(word: Word): Long {
            val list = wordsFlow.value.toMutableList()
            val newId = (list.maxOfOrNull { it.id } ?: 0) + 1
            list.add(word.copy(id = newId))
            wordsFlow.value = list
            return newId.toLong()
        }

        override suspend fun updateWord(word: Word) {
            val list = wordsFlow.value.toMutableList()
            val index = list.indexOfFirst { it.id == word.id }
            if (index != -1) {
                list[index] = word
                wordsFlow.value = list
            }
        }

        override suspend fun deleteWordById(id: Int) {
            val list = wordsFlow.value.toMutableList()
            list.removeAll { it.id == id }
            wordsFlow.value = list
        }

        fun setWords(words: List<Word>) {
            wordsFlow.value = words
        }

        fun getWords(): List<Word> = wordsFlow.value
    }

    // Fake DictionaryRepository Implementation
    private class FakeDictionaryRepository : DictionaryRepository {
        var resultToReturn: Result<Word>? = null

        override suspend fun lookupWord(word: String): Result<Word> {
            return resultToReturn ?: Result.failure(Exception("Not found"))
        }
    }
}
