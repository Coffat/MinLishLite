package com.example.minlishlite.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.minlishlite.data.local.database.AppDatabase
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.WordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var wordDao: WordDao
    private var deckId: Int = 0

    @Before
    fun setup() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        wordDao = database.wordDao()
        
        deckId = database.deckDao().insertDeck(
            DeckEntity(name = "Test Deck", description = "", tag = "", createdAt = 0, updatedAt = 0)
        ).toInt()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun createWordEntity(
        word: String,
        nextReviewAt: Long = 0,
        reviewCount: Int = 0,
        correctCount: Int = 0
    ) = WordEntity(
        deckId = deckId,
        word = word,
        pronunciation = "",
        meaning = "Meaning of $word",
        description = "",
        example = "",
        collocation = "",
        relatedWords = "",
        note = "",
        level = "",
        nextReviewAt = nextReviewAt,
        reviewCount = reviewCount,
        correctCount = correctCount,
        createdAt = 0,
        updatedAt = 0
    )

    @Test
    fun insertAndGetWord() = runTest {
        val word = createWordEntity("Apple")
        val id = wordDao.insertWord(word).toInt()
        
        val retrieved = wordDao.getWordById(id)
        assertNotNull(retrieved)
        assertEquals("Apple", retrieved?.word)
    }

    @Test
    fun updateWord() = runTest {
        val word = createWordEntity("Apple")
        val id = wordDao.insertWord(word).toInt()
        
        val updated = wordDao.getWordById(id)!!.copy(word = "Banana")
        wordDao.updateWord(updated)
        
        val retrieved = wordDao.getWordById(id)
        assertEquals("Banana", retrieved?.word)
    }

    @Test
    fun deleteWord() = runTest {
        val word = createWordEntity("Apple")
        val id = wordDao.insertWord(word).toInt()
        
        wordDao.deleteWordById(id)
        
        assertNull(wordDao.getWordById(id))
    }

    @Test
    fun observeWordsByDeckId_ordersByWord() = runTest {
        wordDao.insertWord(createWordEntity("Banana"))
        wordDao.insertWord(createWordEntity("Apple"))
        
        val words = wordDao.observeWordsByDeckId(deckId).first()
        assertEquals(2, words.size)
        assertEquals("Apple", words[0].word)
        assertEquals("Banana", words[1].word)
    }

    @Test
    fun observeWordsDueToday() = runTest {
        val currentTime = 1000L
        wordDao.insertWord(createWordEntity("Due1", nextReviewAt = 500L))
        wordDao.insertWord(createWordEntity("Due2", nextReviewAt = 1000L))
        wordDao.insertWord(createWordEntity("NotDue", nextReviewAt = 1500L))

        val dueWords = wordDao.observeWordsDueToday(currentTime).first()
        assertEquals(2, dueWords.size)
    }

    @Test
    fun testCounts() = runTest {
        wordDao.insertWord(createWordEntity("Unreviewed", reviewCount = 0))
        wordDao.insertWord(createWordEntity("Reviewed1", reviewCount = 1, correctCount = 1))
        wordDao.insertWord(createWordEntity("Reviewed2", reviewCount = 2, correctCount = 1))

        assertEquals(3, wordDao.observeTotalWordCount().first())
        assertEquals(1, wordDao.observeUnreviewedWordsCount().first())
        assertEquals(2, wordDao.observeTotalWordsLearnedCount().first())
        assertEquals(2, wordDao.observeTotalCorrectCount().first())
        assertEquals(3, wordDao.observeTotalReviewCount().first())
    }
}
