package com.example.minlishlite.data.repository

import com.example.minlishlite.data.local.dao.WordDao
import com.example.minlishlite.data.mapper.toDomain
import com.example.minlishlite.data.mapper.toEntity
import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WordRepositoryImpl(
    private val wordDao: WordDao
) : WordRepository {
    override fun observeWordsByDeckId(deckId: Int): Flow<List<Word>> {
        return wordDao.observeWordsByDeckId(deckId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeWordsDueToday(currentTime: Long): Flow<List<Word>> {
        return wordDao.observeWordsDueToday(currentTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getWordById(id: Int): Word? {
        return wordDao.getWordById(id)?.toDomain()
    }

    override suspend fun insertWord(word: Word): Long {
        return wordDao.insertWord(word.toEntity())
    }

    override suspend fun updateWord(word: Word) {
        wordDao.updateWord(word.toEntity())
    }

    override suspend fun deleteWordById(id: Int) {
        wordDao.deleteWordById(id)
    }
}
