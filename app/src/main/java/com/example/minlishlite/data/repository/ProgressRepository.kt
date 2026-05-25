package com.example.minlishlite.data.repository

import com.example.minlishlite.core.util.AppLogger
import com.example.minlishlite.data.local.dao.ReviewHistoryDao
import com.example.minlishlite.data.local.dao.WordDao
import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import com.example.minlishlite.data.model.ProgressAnalytics
import com.example.minlishlite.core.util.ProgressCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

class ProgressRepository(
    private val wordDao: WordDao,
    private val reviewHistoryDao: ReviewHistoryDao
) {
    fun observeTotalWordsLearned(): Flow<Int> {
        return wordDao.observeTotalWordsLearnedCount()
    }

    fun observeDueTodayCount(currentTime: Long): Flow<Int> {
        return wordDao.observeWordsDueTodayCount(currentTime)
    }

    fun observeReviewHistory(): Flow<List<ReviewHistoryEntity>> {
        return reviewHistoryDao.observeReviewHistory()
    }

    fun observeProgressAnalytics(currentTime: Long = System.currentTimeMillis()): Flow<ProgressAnalytics> {
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
        }.catch { throwable ->
            AppLogger.e("observeProgressAnalytics failed", throwable)
            throw throwable
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
