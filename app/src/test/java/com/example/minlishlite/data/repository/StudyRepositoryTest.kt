package com.example.minlishlite.data.repository

import com.example.minlishlite.core.util.AppLogger
import com.example.minlishlite.data.local.dao.ReviewHistoryDao
import com.example.minlishlite.data.local.dao.WordDao
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.data.model.ReviewResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class StudyRepositoryTest {

    private lateinit var wordDao: WordDao
    private lateinit var reviewHistoryDao: ReviewHistoryDao
    private lateinit var studyRepository: StudyRepository

    @Before
    fun setup() {
        wordDao = mockk(relaxed = true)
        reviewHistoryDao = mockk(relaxed = true)
        studyRepository = StudyRepository(wordDao, reviewHistoryDao)
        
        mockkObject(AppLogger)
        every { AppLogger.e(any(), any()) } returns Unit
    }

    @After
    fun teardown() {
        unmockkObject(AppLogger)
    }

    @Test
    fun reviewWord_wordNotFound_doesNothing() = runTest {
        coEvery { wordDao.getWordById(1) } returns null

        studyRepository.reviewWord(1, ReviewResult.GOOD, 1000L, 2.5f)

        coVerify(exactly = 0) { wordDao.updateWord(any()) }
        coVerify(exactly = 0) { reviewHistoryDao.insertHistory(any()) }
    }

    @Test
    fun reviewWord_wordFound_updatesWordAndInsertsHistory() = runTest {
        val word = WordEntity(
            id = 1, deckId = 1, word = "Apple", pronunciation = "", meaning = "Táo",
            description = "", example = "", collocation = "", relatedWords = "",
            note = "", level = "", nextReviewAt = 0, reviewCount = 0, correctCount = 0,
            createdAt = 0, updatedAt = 0
        )
        coEvery { wordDao.getWordById(1) } returns word

        studyRepository.reviewWord(1, ReviewResult.GOOD, 2000L, 2.5f)

        coVerify { 
            wordDao.updateWord(match { 
                it.id == 1 && it.reviewCount == 1 && it.correctCount == 1 && it.nextReviewAt == 2000L 
            }) 
        }
        coVerify { 
            reviewHistoryDao.insertHistory(match { 
                it.wordId == 1 && it.deckId == 1 && it.result == "GOOD" 
            }) 
        }
    }
}
