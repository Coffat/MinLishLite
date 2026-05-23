package com.example.minlishlite.data.remote.dto

data class TranslationResponseDto(
    val responseData: TranslationDataDto? = null
)

data class TranslationDataDto(
    val translatedText: String? = null
)
