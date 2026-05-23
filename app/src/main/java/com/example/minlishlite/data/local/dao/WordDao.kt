package com.example.minlishlite.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.minlishlite.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE deckId = :deckId ORDER BY word ASC")
    fun observeWordsByDeckId(deckId: Int): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE nextReviewAt <= :currentTime ORDER BY nextReviewAt ASC")
    fun observeWordsDueToday(currentTime: Long): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words WHERE reviewCount > 0")
    fun observeTotalWordsLearnedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE nextReviewAt <= :currentTime")
    fun observeWordsDueTodayCount(currentTime: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE reviewCount = 0")
    fun observeUnreviewedWordsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words")
    fun observeTotalWordCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(correctCount), 0) FROM words WHERE reviewCount > 0")
    fun observeTotalCorrectCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(reviewCount), 0) FROM words WHERE reviewCount > 0")
    fun observeTotalReviewCount(): Flow<Int>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Int): WordEntity?

    @Insert
    suspend fun insertWord(word: WordEntity): Long

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteWordById(id: Int)
}
