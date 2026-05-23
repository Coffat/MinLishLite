package com.example.minlishlite.domain.repository

import com.example.minlishlite.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun observeWordsByDeckId(deckId: Int): Flow<List<Word>>
    fun observeWordsDueToday(currentTime: Long): Flow<List<Word>>
    fun observeUnreviewedWordsCount(): Flow<Int>
    suspend fun getWordById(id: Int): Word?
    suspend fun insertWord(word: Word): Long
    suspend fun updateWord(word: Word)
    suspend fun deleteWordById(id: Int)
}
