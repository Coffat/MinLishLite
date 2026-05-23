package com.example.minlishlite.domain.model

data class ReviewHistory(
    val id: Int = 0,
    val wordId: Int,
    val deckId: Int,
    val result: String,
    val reviewedAt: Long
)
