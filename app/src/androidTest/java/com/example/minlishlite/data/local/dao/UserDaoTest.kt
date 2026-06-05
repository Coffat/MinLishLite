package com.example.minlishlite.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.minlishlite.data.local.database.AppDatabase
import com.example.minlishlite.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        userDao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun upsertAndGetUser() = runTest {
        val user = UserEntity(
            id = 1,
            name = "Test User",
            email = "test@example.com",
            goal = "Learn English",
            level = "Beginner",
            createdAt = 1000L
        )
        userDao.upsertUser(user)

        val retrievedUser = userDao.getUser(1)
        assertNotNull(retrievedUser)
        assertEquals("Test User", retrievedUser?.name)
    }

    @Test
    fun observeUser() = runTest {
        val user = UserEntity(
            id = 1,
            name = "Test User",
            email = "test@example.com",
            goal = "Learn English",
            level = "Beginner",
            createdAt = 1000L
        )
        userDao.upsertUser(user)

        val retrievedUser = userDao.observeUser(1).first()
        assertNotNull(retrievedUser)
        assertEquals("test@example.com", retrievedUser?.email)
    }
}
