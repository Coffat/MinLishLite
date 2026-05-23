package com.example.minlishlite.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deckDao(): DeckDao
    abstract fun wordDao(): WordDao
    abstract fun reviewHistoryDao(): ReviewHistoryDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE words ADD COLUMN pronunciationAudioUrl TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE words ADD COLUMN pronunciationUk TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE words ADD COLUMN pronunciationUs TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE words ADD COLUMN pronunciationUkAudioUrl TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE words ADD COLUMN pronunciationUsAudioUrl TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    """
                    UPDATE words
                    SET pronunciationUk = pronunciation,
                        pronunciationUkAudioUrl = pronunciationAudioUrl
                    WHERE pronunciationUk = ''
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "minlish_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
