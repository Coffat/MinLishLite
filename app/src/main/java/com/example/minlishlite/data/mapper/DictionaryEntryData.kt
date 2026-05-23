package com.example.minlishlite.data.mapper

data class EnglishDefinitionLine(
    val partOfSpeech: String,
    val text: String
)

data class DictionaryEntryData(
    val word: String,
    val pronunciationUk: String,
    val pronunciationUs: String,
    val pronunciationUkAudioUrl: String,
    val pronunciationUsAudioUrl: String,
    val englishDefinitions: List<EnglishDefinitionLine>,
    val example: String,
    val relatedWords: String,
    val primaryPartOfSpeech: String
)
