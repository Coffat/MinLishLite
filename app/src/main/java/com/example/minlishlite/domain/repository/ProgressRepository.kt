package com.example.minlishlite.domain.repository

import com.example.minlishlite.domain.model.ProgressAnalytics
import com.example.minlishlite.domain.model.ReviewHistory
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeTotalWordsLearned(): Flow<Int>
    fun observeDueTodayCount(currentTime: Long): Flow<Int>
    fun observeReviewHistory(): Flow<List<ReviewHistory>>
    fun observeProgressAnalytics(currentTime: Long = System.currentTimeMillis()): Flow<ProgressAnalytics>
}
