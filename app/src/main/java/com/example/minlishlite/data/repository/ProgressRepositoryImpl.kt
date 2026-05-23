package com.example.minlishlite.data.repository

import com.example.minlishlite.data.local.dao.ReviewHistoryDao
import com.example.minlishlite.data.local.dao.WordDao
import com.example.minlishlite.data.mapper.toDomain
import com.example.minlishlite.domain.model.ProgressAnalytics
import com.example.minlishlite.domain.model.ReviewHistory
import com.example.minlishlite.domain.repository.ProgressRepository
import com.example.minlishlite.domain.usecase.ProgressCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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

    override fun observeProgressAnalytics(currentTime: Long): Flow<ProgressAnalytics> {
        val wordCounts = combine(
            wordDao.observeTotalWordCount(),
            wordDao.observeTotalWordsLearnedCount(),
            wordDao.observeWordsDueTodayCount(currentTime),
            wordDao.observeTotalCorrectCount(),
            wordDao.observeTotalReviewCount()
        ) { totalWords, wordsLearned, dueToday, totalCorrect, totalReviews ->
            WordCountSnapshot(
                totalWords = totalWords,
                wordsLearned = wordsLearned,
                dueToday = dueToday,
                totalCorrect = totalCorrect,
                totalReviews = totalReviews
            )
        }

        return combine(wordCounts, observeReviewHistory()) { counts, history ->
            ProgressCalculator.compute(
                totalWords = counts.totalWords,
                wordsLearned = counts.wordsLearned,
                dueToday = counts.dueToday,
                totalCorrect = counts.totalCorrect,
                totalReviews = counts.totalReviews,
                reviewHistory = history,
                now = currentTime
            )
        }
    }

    private data class WordCountSnapshot(
        val totalWords: Int,
        val wordsLearned: Int,
        val dueToday: Int,
        val totalCorrect: Int,
        val totalReviews: Int
    )
}
