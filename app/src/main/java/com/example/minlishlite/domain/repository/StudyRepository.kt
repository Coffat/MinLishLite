package com.example.minlishlite.domain.repository

import com.example.minlishlite.domain.model.ReviewHistory
import com.example.minlishlite.domain.model.ReviewResult
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    suspend fun reviewWord(
        wordId: Int,
        result: ReviewResult,
        nextReviewAt: Long,
        easeFactor: Float
    )

    fun observeReviewHistory(): Flow<List<ReviewHistory>>
}
