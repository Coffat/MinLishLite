package com.example.minlishlite.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewHistoryDao {
    @Query("SELECT * FROM review_history ORDER BY reviewedAt DESC")
    fun observeReviewHistory(): Flow<List<ReviewHistoryEntity>>

    @Insert
    suspend fun insertHistory(history: ReviewHistoryEntity)
}
