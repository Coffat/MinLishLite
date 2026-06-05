package com.example.minlishlite.data.repository

import com.example.minlishlite.data.local.dao.WordDao
import com.example.minlishlite.data.local.entity.WordEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WordRepositoryTest {

    private lateinit var wordDao: WordDao
    private lateinit var wordRepository: WordRepository

    @Before
    fun setup() {
        wordDao = mockk(relaxed = true)
        wordRepository = WordRepository(wordDao)
    }

    @Test
    fun getWordById_returnsWordFromDao() = runTest {
        val word = WordEntity(
            id = 1, deckId = 1, word = "Apple", pronunciation = "", meaning = "Táo",
            description = "", example = "", collocation = "", relatedWords = "",
            note = "", level = "", nextReviewAt = 0, createdAt = 0, updatedAt = 0
        )
        coEvery { wordDao.getWordById(1) } returns word

        val result = wordRepository.getWordById(1)
        
        assertEquals(word, result)
        coVerify { wordDao.getWordById(1) }
    }

    @Test
    fun insertWord_delegatesToDao() = runTest {
        val word = WordEntity(
            id = 1, deckId = 1, word = "Apple", pronunciation = "", meaning = "Táo",
            description = "", example = "", collocation = "", relatedWords = "",
            note = "", level = "", nextReviewAt = 0, createdAt = 0, updatedAt = 0
        )
        coEvery { wordDao.insertWord(word) } returns 1L

        val id = wordRepository.insertWord(word)
        
        assertEquals(1L, id)
        coVerify { wordDao.insertWord(word) }
    }
}
