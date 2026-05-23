package com.example.minlishlite.data.mapper

fun String.toVietnamesePartOfSpeech(): String {
    return when (trim().lowercase()) {
        "noun" -> "danh từ"
        "verb" -> "động từ"
        "adjective" -> "tính từ"
        "adverb" -> "trạng từ"
        "pronoun" -> "đại từ"
        "preposition" -> "giới từ"
        "conjunction" -> "liên từ"
        "interjection" -> "thán từ"
        "exclamation" -> "thán từ"
        else -> trim()
    }
}
