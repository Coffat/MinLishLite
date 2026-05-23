package com.example.minlishlite.domain.repository

import com.example.minlishlite.domain.model.Deck
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    fun observeAllDecks(): Flow<List<Deck>>
    suspend fun getDeckById(id: Int): Deck?
    suspend fun insertDeck(deck: Deck): Long
    suspend fun updateDeck(deck: Deck)
    suspend fun deleteDeckById(id: Int)
}
