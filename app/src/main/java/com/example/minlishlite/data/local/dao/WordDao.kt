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

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Int): WordEntity?

    @Insert
    suspend fun insertWord(word: WordEntity): Long

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteWordById(id: Int)
}
