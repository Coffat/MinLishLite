package com.example.minlishlite.data.repository

import com.example.minlishlite.data.local.dao.UserDao
import com.example.minlishlite.data.mapper.toDomain
import com.example.minlishlite.data.mapper.toEntity
import com.example.minlishlite.domain.model.User
import com.example.minlishlite.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    override fun observeUser(): Flow<User?> {
        return userDao.observeUser().map { it?.toDomain() }
    }

    override suspend fun saveUser(user: User) {
        userDao.upsertUser(user.toEntity())
    }
}
