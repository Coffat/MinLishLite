package com.example.minlishlite.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.minlishlite.data.local.database.AppDatabase
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import com.example.minlishlite.data.local.entity.WordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewHistoryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var reviewHistoryDao: ReviewHistoryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        reviewHistoryDao = database.reviewHistoryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndObserveReviewHistory() = runTest {
        // Must insert Deck and Word first due to Foreign Key constraints
        val deckId = database.deckDao().insertDeck(
            DeckEntity(name = "Deck", description = "", tag = "", createdAt = 0, updatedAt = 0)
        ).toInt()

        val wordId = database.wordDao().insertWord(
            WordEntity(
                deckId = deckId,
                word = "Apple",
                pronunciation = "",
                meaning = "Táo",
                description = "",
                example = "",
                collocation = "",
                relatedWords = "",
                note = "",
                level = "",
                nextReviewAt = 0,
                createdAt = 0,
                updatedAt = 0
            )
        ).toInt()

        val history1 = ReviewHistoryEntity(wordId = wordId, deckId = deckId, result = "GOOD", reviewedAt = 1000L)
        val history2 = ReviewHistoryEntity(wordId = wordId, deckId = deckId, result = "EASY", reviewedAt = 2000L)

        reviewHistoryDao.insertHistory(history1)
        reviewHistoryDao.insertHistory(history2)

        val historyList = reviewHistoryDao.observeReviewHistory().first()
        assertEquals(2, historyList.size)
        // Order by reviewedAt DESC
        assertEquals("EASY", historyList[0].result)
        assertEquals("GOOD", historyList[1].result)
    }
}
