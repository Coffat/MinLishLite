package com.example.minlishlite.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.minlishlite.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun observeUser(id: Int = 1): Flow<UserEntity?>

    @Upsert
    suspend fun upsertUser(user: UserEntity)
}
