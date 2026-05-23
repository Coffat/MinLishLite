package com.example.minlishlite.domain.model

data class Word(
    val id: Int = 0,
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
