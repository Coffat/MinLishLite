package com.example.minlishlite.domain.model

data class User(
    val id: Int = 1,
    val name: String,
    val email: String?,
    val goal: String,
    val level: String,
    val createdAt: Long
)
