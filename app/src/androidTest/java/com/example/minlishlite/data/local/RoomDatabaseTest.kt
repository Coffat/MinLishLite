package com.example.minlishlite.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.minlishlite.data.local.database.AppDatabase
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import com.example.minlishlite.data.local.entity.UserEntity
import com.example.minlishlite.data.local.entity.WordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun createUser_upsertAndObserve_success() = runBlocking {
        val userDao = db.userDao()
        val user = UserEntity(
            id = 1,
            name = "John Doe",
            email = "john@example.com",
            goal = "Learn 10 words daily",
            level = "Intermediate",
            createdAt = System.currentTimeMillis()
        )

        userDao.upsertUser(user)

        val observedUser = userDao.observeUser(1).first()
        assertNotNull(observedUser)
        assertEquals("John Doe", observedUser?.name)
        assertEquals("john@example.com", observedUser?.email)

        // Test upsert (update)
        val updatedUser = user.copy(name = "Jane Doe")
        userDao.upsertUser(updatedUser)

        val observedUpdatedUser = userDao.observeUser(1).first()
        assertEquals("Jane Doe", observedUpdatedUser?.name)
    }

    @Test
    fun createDeck_insertAndObserve_success() = runBlocking {
        val deckDao = db.deckDao()
        val deck = DeckEntity(
            name = "TOEIC Essential",
            description = "Must know words for TOEIC",
            tag = "TOEIC",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val deckId = deckDao.insertDeck(deck)
        assertTrue(deckId > 0)

        val observedDecks = deckDao.observeAllDecks().first()
        assertEquals(1, observedDecks.size)
        assertEquals("TOEIC Essential", observedDecks[0].name)

        val fetchedDeck = deckDao.getDeckById(deckId.toInt())
        assertNotNull(fetchedDeck)
        assertEquals("TOEIC Essential", fetchedDeck?.name)
    }

    @Test
    fun createWord_queryByDeckId_success() = runBlocking {
        val deckDao = db.deckDao()
        val wordDao = db.wordDao()

        val deckId = deckDao.insertDeck(
            DeckEntity(
                name = "A",
                description = "Desc",
                tag = "Tag",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        ).toInt()

        val word = WordEntity(
            deckId = deckId,
            word = "Abundant",
            pronunciation = "/əˈbʌndənt/",
            meaning = "Existing or available in large quantities",
            description = "Common vocabulary word",
            example = "There is abundant evidence of climate change.",
            collocation = "abundant water, abundant evidence",
            relatedWords = "abundance, abound",
            note = "Important for exam",
            level = "B2",
            nextReviewAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val wordId = wordDao.insertWord(word)
        assertTrue(wordId > 0)

        val observedWords = wordDao.observeWordsByDeckId(deckId).first()
        assertEquals(1, observedWords.size)
        assertEquals("Abundant", observedWords[0].word)

        val fetchedWord = wordDao.getWordById(wordId.toInt())
        assertEquals("Abundant", fetchedWord?.word)
    }

    @Test
    fun deleteDeck_cascadeDeletesWords_success() = runBlocking {
        val deckDao = db.deckDao()
        val wordDao = db.wordDao()

        val deckId = deckDao.insertDeck(
            DeckEntity(
                name = "Deck to Delete",
                description = "Desc",
                tag = "Tag",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        ).toInt()

        val word = WordEntity(
            deckId = deckId,
            word = "DeleteMe",
            pronunciation = "...",
            meaning = "...",
            description = "...",
            example = "...",
            collocation = "...",
            relatedWords = "...",
            note = "...",
            level = "A1",
            nextReviewAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        wordDao.insertWord(word)

        // Verify word exists
        val wordsBefore = wordDao.observeWordsByDeckId(deckId).first()
        assertEquals(1, wordsBefore.size)

        // Delete Deck
        deckDao.deleteDeckById(deckId)

        // Verify deck is deleted
        assertNull(deckDao.getDeckById(deckId))

        // Verify word is cascade deleted
        val wordsAfter = wordDao.observeWordsByDeckId(deckId).first()
        assertTrue(wordsAfter.isEmpty())
    }

    @Test
    fun deleteWord_cascadeDeletesReviewHistory_success() = runBlocking {
        val deckDao = db.deckDao()
        val wordDao = db.wordDao()
        val reviewHistoryDao = db.reviewHistoryDao()

        val deckId = deckDao.insertDeck(
            DeckEntity(
                name = "Deck A",
                description = "Desc",
                tag = "Tag",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        ).toInt()

        val wordId = wordDao.insertWord(
            WordEntity(
                deckId = deckId,
                word = "Temp",
                pronunciation = "...",
                meaning = "...",
                description = "...",
                example = "...",
                collocation = "...",
                relatedWords = "...",
                note = "...",
                level = "A1",
                nextReviewAt = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        ).toInt()

        val reviewHistory = ReviewHistoryEntity(
            wordId = wordId,
            deckId = deckId,
            result = "Good",
            reviewedAt = System.currentTimeMillis()
        )
        reviewHistoryDao.insertHistory(reviewHistory)

        // Verify history exists
        val historyBefore = reviewHistoryDao.observeReviewHistory().first()
        assertEquals(1, historyBefore.size)

        // Delete Word
        wordDao.deleteWordById(wordId)

        // Verify review history is cascade deleted
        val historyAfter = reviewHistoryDao.observeReviewHistory().first()
        assertTrue(historyAfter.isEmpty())
    }

    @Test
    fun queryDueToday_returnsOnlyDueWords() = runBlocking {
        val deckDao = db.deckDao()
        val wordDao = db.wordDao()

        val deckId = deckDao.insertDeck(
            DeckEntity(
                name = "Deck B",
                description = "Desc",
                tag = "Tag",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        ).toInt()

        val currentTime = System.currentTimeMillis()

        // Insert word due in past (due today)
        val wordDue = WordEntity(
            deckId = deckId,
            word = "DueWord",
            pronunciation = "...",
            meaning = "...",
            description = "...",
            example = "...",
            collocation = "...",
            relatedWords = "...",
            note = "...",
            level = "A1",
            nextReviewAt = currentTime - 10000, // 10s in past
            createdAt = currentTime,
            updatedAt = currentTime
        )
        wordDao.insertWord(wordDue)

        // Insert word due in future (not due today)
        val wordFuture = WordEntity(
            deckId = deckId,
            word = "FutureWord",
            pronunciation = "...",
            meaning = "...",
            description = "...",
            example = "...",
            collocation = "...",
            relatedWords = "...",
            note = "...",
            level = "A1",
            nextReviewAt = currentTime + 100000, // 100s in future
            createdAt = currentTime,
            updatedAt = currentTime
        )
        wordDao.insertWord(wordFuture)

        // Query due words
        val dueWords = wordDao.observeWordsDueToday(currentTime).first()
        assertEquals(1, dueWords.size)
        assertEquals("DueWord", dueWords[0].word)
    }
}
