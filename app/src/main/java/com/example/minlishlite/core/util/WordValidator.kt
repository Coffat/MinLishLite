package com.example.minlishlite.core.util

object WordValidator {
    fun validateWord(word: String, meaning: String): Boolean {
        return word.isNotBlank() && meaning.isNotBlank()
    }
}
