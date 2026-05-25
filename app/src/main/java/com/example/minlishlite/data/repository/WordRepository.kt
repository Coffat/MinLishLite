package com.example.minlishlite.data.repository

import com.example.minlishlite.data.local.dao.WordDao
import com.example.minlishlite.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

class WordRepository(
    private val wordDao: WordDao
) {
    fun observeWordsByDeckId(deckId: Int): Flow<List<WordEntity>> {
        return wordDao.observeWordsByDeckId(deckId)
    }

    fun observeWordsDueToday(currentTime: Long): Flow<List<WordEntity>> {
        return wordDao.observeWordsDueToday(currentTime)
    }

    fun observeUnreviewedWordsCount(): Flow<Int> {
        return wordDao.observeUnreviewedWordsCount()
    }

    suspend fun getWordById(id: Int): WordEntity? {
        return wordDao.getWordById(id)
    }

    suspend fun insertWord(word: WordEntity): Long {
        return wordDao.insertWord(word)
    }

    suspend fun updateWord(word: WordEntity) {
        wordDao.updateWord(word)
    }

    suspend fun deleteWordById(id: Int) {
        wordDao.deleteWordById(id)
    }
}
