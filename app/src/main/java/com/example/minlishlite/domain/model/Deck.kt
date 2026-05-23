package com.example.minlishlite.domain.model

data class Deck(
    val id: Int = 0,
    val name: String,
    val description: String,
    val tag: String,
    val createdAt: Long,
    val updatedAt: Long
)
