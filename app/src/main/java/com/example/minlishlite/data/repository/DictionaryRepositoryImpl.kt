package com.example.minlishlite.data.repository

import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.domain.repository.DictionaryRepository
import kotlinx.coroutines.delay

class DictionaryRepositoryImpl : DictionaryRepository {
    override suspend fun lookupWord(word: String): Result<Word> {
        if (word.isBlank()) {
            return Result.failure(IllegalArgumentException("Word cannot be blank"))
        }

        // Simulate network delay
        delay(800)

        val normalizedWord = word.trim().lowercase()

        val mockWord = when (normalizedWord) {
            "hello" -> Word(
                deckId = 0,
                word = "hello",
                pronunciation = "/həˈləʊ/",
                meaning = "Xin chào",
                description = "Used as a greeting when you meet somebody.",
                example = "Hello, how are you?",
                collocation = "say hello to someone",
                relatedWords = "hi, hey, greetings",
                note = "Common greeting",
                level = "Beginner",
                nextReviewAt = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            "world" -> Word(
                deckId = 0,
                word = "world",
                pronunciation = "/wɜːld/",
                meaning = "Thế giới",
                description = "The earth, together with all of its countries and peoples.",
                example = "He wants to travel around the world.",
                collocation = "all over the world",
                relatedWords = "earth, globe, planet",
                note = "Common noun",
                level = "Beginner",
                nextReviewAt = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            "study" -> Word(
                deckId = 0,
                word = "study",
                pronunciation = "/ˈstʌdi/",
                meaning = "Học tập, nghiên cứu",
                description = "The devotion of time and attention to acquiring knowledge.",
                example = "She needs to study for her exams.",
                collocation = "study hard, case study",
                relatedWords = "learn, research, examine",
                note = "Verb and noun",
                level = "Beginner",
                nextReviewAt = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            else -> Word(
                deckId = 0,
                word = word.trim(),
                pronunciation = "/.../",
                meaning = "Nghĩa của từ '$word' (Tra cứu giả lập)",
                description = "This is a simulated definition for '$word' from the mock dictionary service.",
                example = "Example sentence containing '$word'.",
                collocation = "collocation of '$word'",
                relatedWords = "related, words",
                note = "Mocked result",
                level = "Intermediate",
                nextReviewAt = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }

        return Result.success(mockWord)
    }
}
