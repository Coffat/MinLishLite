package com.example.minlishlite.domain.repository

import com.example.minlishlite.domain.model.ReviewHistory
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    suspend fun reviewWord(wordId: Int, result: String, nextReviewAt: Long)
    fun observeReviewHistory(): Flow<List<ReviewHistory>>
}
