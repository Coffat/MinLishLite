package com.example.minlishlite.data.repository

import com.example.minlishlite.data.local.dao.DeckDao
import com.example.minlishlite.data.local.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

class DeckRepository(
    private val deckDao: DeckDao
) {
    fun observeAllDecks(): Flow<List<DeckEntity>> {
        return deckDao.observeAllDecks()
    }

    suspend fun getDeckById(id: Int): DeckEntity? {
        return deckDao.getDeckById(id)
    }

    suspend fun insertDeck(deck: DeckEntity): Long {
        return deckDao.insertDeck(deck)
    }

    suspend fun updateDeck(deck: DeckEntity) {
        deckDao.updateDeck(deck)
    }

    suspend fun deleteDeckById(id: Int) {
        deckDao.deleteDeckById(id)
    }
}
