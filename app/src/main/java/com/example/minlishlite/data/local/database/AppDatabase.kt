package com.example.minlishlite.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.minlishlite.data.local.dao.DeckDao
import com.example.minlishlite.data.local.dao.ReviewHistoryDao
import com.example.minlishlite.data.local.dao.UserDao
import com.example.minlishlite.data.local.dao.WordDao
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import com.example.minlishlite.data.local.entity.UserEntity
import com.example.minlishlite.data.local.entity.WordEntity

@Database(
    entities = [
        UserEntity::class,
        DeckEntity::class,
        WordEntity::class,
        ReviewHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deckDao(): DeckDao
    abstract fun wordDao(): WordDao
    abstract fun reviewHistoryDao(): ReviewHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "minlish_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
