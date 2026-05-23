package com.example.minlishlite.data.repository

import com.example.minlishlite.core.util.AppLogger
import com.example.minlishlite.data.local.dao.ReviewHistoryDao
import com.example.minlishlite.data.local.dao.WordDao
import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import com.example.minlishlite.data.mapper.toDomain
import com.example.minlishlite.domain.model.ReviewHistory
import com.example.minlishlite.domain.model.ReviewResult
import com.example.minlishlite.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudyRepositoryImpl(
    private val wordDao: WordDao,
    private val reviewHistoryDao: ReviewHistoryDao
) : StudyRepository {

    override suspend fun reviewWord(
        wordId: Int,
        result: ReviewResult,
        nextReviewAt: Long,
        easeFactor: Float
    ) {
        val now = System.currentTimeMillis()
        val wordEntity = wordDao.getWordById(wordId)
        if (wordEntity == null) {
            AppLogger.e("reviewWord: word not found for id=$wordId")
            return
        }

        val updatedWord = wordEntity.copy(
            reviewCount = wordEntity.reviewCount + 1,
            correctCount = if (result != ReviewResult.AGAIN) {
                wordEntity.correctCount + 1
            } else {
                wordEntity.correctCount
            },
            easeFactor = easeFactor,
            nextReviewAt = nextReviewAt,
            lastReviewedAt = now,
            updatedAt = now
        )
        wordDao.updateWord(updatedWord)

        val history = ReviewHistoryEntity(
            wordId = wordId,
            deckId = wordEntity.deckId,
            result = result.name,
            reviewedAt = now
        )
        reviewHistoryDao.insertHistory(history)
    }

    override fun observeReviewHistory(): Flow<List<ReviewHistory>> {
        return reviewHistoryDao.observeReviewHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
