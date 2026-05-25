package com.example.minlishlite.data.repository

import com.example.minlishlite.data.local.dao.UserDao
import com.example.minlishlite.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {
    fun observeUser(): Flow<UserEntity?> {
        return userDao.observeUser()
    }

    suspend fun saveUser(user: UserEntity) {
        userDao.upsertUser(user)
    }
}
