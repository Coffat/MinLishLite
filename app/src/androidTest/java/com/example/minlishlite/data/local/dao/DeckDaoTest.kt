package com.example.minlishlite.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.minlishlite.data.local.database.AppDatabase
import com.example.minlishlite.data.local.entity.DeckEntity
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
class DeckDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var deckDao: DeckDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        deckDao = database.deckDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetDeck() = runTest {
        val deck = DeckEntity(
            name = "Test Deck",
            description = "Description",
            tag = "Tag",
            createdAt = 1000L,
            updatedAt = 1000L
        )
        val id = deckDao.insertDeck(deck).toInt()
        
        val retrievedDeck = deckDao.getDeckById(id)
        assertNotNull(retrievedDeck)
        assertEquals("Test Deck", retrievedDeck?.name)
    }

    @Test
    fun updateDeck() = runTest {
        val deck = DeckEntity(
            name = "Test Deck",
            description = "Description",
            tag = "Tag",
            createdAt = 1000L,
            updatedAt = 1000L
        )
        val id = deckDao.insertDeck(deck).toInt()
        
        val updatedDeck = deck.copy(id = id, name = "Updated Deck")
        deckDao.updateDeck(updatedDeck)
        
        val retrievedDeck = deckDao.getDeckById(id)
        assertEquals("Updated Deck", retrievedDeck?.name)
    }

    @Test
    fun deleteDeck() = runTest {
        val deck = DeckEntity(
            name = "Test Deck",
            description = "Description",
            tag = "Tag",
            createdAt = 1000L,
            updatedAt = 1000L
        )
        val id = deckDao.insertDeck(deck).toInt()
        
        deckDao.deleteDeckById(id)
        
        val retrievedDeck = deckDao.getDeckById(id)
        assertNull(retrievedDeck)
    }

    @Test
    fun observeAllDecks_ordersByName() = runTest {
        deckDao.insertDeck(DeckEntity(name = "B", description = "", tag = "", createdAt = 0, updatedAt = 0))
        deckDao.insertDeck(DeckEntity(name = "A", description = "", tag = "", createdAt = 0, updatedAt = 0))
        
        val decks = deckDao.observeAllDecks().first()
        assertEquals(2, decks.size)
        assertEquals("A", decks[0].name)
        assertEquals("B", decks[1].name)
    }
}
