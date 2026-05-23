package com.example.minlishlite.domain.repository

import com.example.minlishlite.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(): Flow<User?>
    suspend fun saveUser(user: User)
}
