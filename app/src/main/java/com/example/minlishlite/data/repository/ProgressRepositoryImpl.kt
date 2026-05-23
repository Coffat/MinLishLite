package com.example.minlishlite.data.repository

import com.example.minlishlite.data.local.dao.ReviewHistoryDao
import com.example.minlishlite.data.local.dao.WordDao
import com.example.minlishlite.data.mapper.toDomain
import com.example.minlishlite.domain.model.ReviewHistory
import com.example.minlishlite.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProgressRepositoryImpl(
    private val wordDao: WordDao,
    private val reviewHistoryDao: ReviewHistoryDao
) : ProgressRepository {

    override fun observeTotalWordsLearned(): Flow<Int> {
        return wordDao.observeTotalWordsLearnedCount()
    }

    override fun observeDueTodayCount(currentTime: Long): Flow<Int> {
        return wordDao.observeWordsDueTodayCount(currentTime)
    }

    override fun observeReviewHistory(): Flow<List<ReviewHistory>> {
        return reviewHistoryDao.observeReviewHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
