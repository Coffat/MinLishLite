package com.example.minlishlite.domain.repository

interface TranslationRepository {
    suspend fun translateEnToVi(text: String): Result<String>
}
