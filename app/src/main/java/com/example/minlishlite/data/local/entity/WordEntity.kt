package com.example.minlishlite.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["deckId"]),
        Index(value = ["nextReviewAt"])
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId: Int,
    val word: String,
    val pronunciation: String,
    val pronunciationUk: String = "",
    val pronunciationUs: String = "",
    val pronunciationUkAudioUrl: String = "",
    val pronunciationUsAudioUrl: String = "",
    val pronunciationAudioUrl: String = "",
    val meaning: String,
    val description: String,
    val example: String,
    val collocation: String,
    val relatedWords: String,
    val note: String,
    val level: String,
    val easeFactor: Float = 2.5f,
    val nextReviewAt: Long,
    val lastReviewedAt: Long? = null,
    val reviewCount: Int = 0,
    val correctCount: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
