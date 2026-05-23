package com.example.minlishlite.domain.repository

import com.example.minlishlite.domain.model.Word

interface DictionaryRepository {
    suspend fun lookupWord(word: String): Result<Word>
}
