package com.example.minlishlite.data.repository

import com.example.minlishlite.data.local.dao.DeckDao
import com.example.minlishlite.data.mapper.toDomain
import com.example.minlishlite.data.mapper.toEntity
import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.domain.repository.DeckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeckRepositoryImpl(
    private val deckDao: DeckDao
) : DeckRepository {
    override fun observeAllDecks(): Flow<List<Deck>> {
        return deckDao.observeAllDecks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDeckById(id: Int): Deck? {
        return deckDao.getDeckById(id)?.toDomain()
    }

    override suspend fun insertDeck(deck: Deck): Long {
        return deckDao.insertDeck(deck.toEntity())
    }

    override suspend fun updateDeck(deck: Deck) {
        deckDao.updateDeck(deck.toEntity())
    }

    override suspend fun deleteDeckById(id: Int) {
        deckDao.deleteDeckById(id)
    }
}
