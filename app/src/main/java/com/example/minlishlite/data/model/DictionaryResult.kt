package com.example.minlishlite.data.model

data class DictionaryResult(
    val word: String = "",
    val pronunciationUk: String? = null,
    val pronunciationUs: String? = null,
    val pronunciationUkAudioUrl: String? = null,
    val pronunciationUsAudioUrl: String? = null,
    val meaning: String = "",
    val definition: String = "",
    val partOfSpeech: String = "",
    val example: String? = null
)
