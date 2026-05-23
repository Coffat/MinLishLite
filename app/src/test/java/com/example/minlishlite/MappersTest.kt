package com.example.minlishlite

import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import com.example.minlishlite.data.local.entity.UserEntity
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.data.mapper.toDomain
import com.example.minlishlite.data.mapper.toEntity
import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.domain.model.ReviewHistory
import com.example.minlishlite.domain.model.User
import com.example.minlishlite.domain.model.Word
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {

    @Test
    fun userEntity_toDomain_mapsCorrectly() {
        val entity = UserEntity(
            id = 1,
            name = "Test User",
            email = "test@example.com",
            goal = "10 words",
            level = "Intermediate",
            createdAt = 123456789L
        )
        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.email, domain.email)
        assertEquals(entity.goal, domain.goal)
        assertEquals(entity.level, domain.level)
        assertEquals(entity.createdAt, domain.createdAt)
    }

    @Test
    fun user_toEntity_mapsCorrectly() {
        val domain = User(
            id = 1,
            name = "Test User",
            email = "test@example.com",
            goal = "10 words",
            level = "Intermediate",
            createdAt = 123456789L
        )
        val entity = domain.toEntity()

        assertEquals(domain.id, entity.id)
        assertEquals(domain.name, entity.name)
        assertEquals(domain.email, entity.email)
        assertEquals(domain.goal, entity.goal)
        assertEquals(domain.level, entity.level)
        assertEquals(domain.createdAt, entity.createdAt)
    }

    @Test
    fun deckEntity_toDomain_mapsCorrectly() {
        val entity = DeckEntity(
            id = 10,
            name = "Test Deck",
            description = "Description",
            tag = "TestTag",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.description, domain.description)
        assertEquals(entity.tag, domain.tag)
        assertEquals(entity.createdAt, domain.createdAt)
        assertEquals(entity.updatedAt, domain.updatedAt)
    }

    @Test
    fun deck_toEntity_mapsCorrectly() {
        val domain = Deck(
            id = 10,
            name = "Test Deck",
            description = "Description",
            tag = "TestTag",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        val entity = domain.toEntity()

        assertEquals(domain.id, entity.id)
        assertEquals(domain.name, entity.name)
        assertEquals(domain.description, entity.description)
        assertEquals(domain.tag, entity.tag)
        assertEquals(domain.createdAt, entity.createdAt)
        assertEquals(domain.updatedAt, entity.updatedAt)
    }

    @Test
    fun wordEntity_toDomain_mapsCorrectly() {
        val entity = WordEntity(
            id = 100,
            deckId = 5,
            word = "apple",
            pronunciation = "/ˈæpl/",
            meaning = "quả táo",
            description = "A round fruit",
            example = "He ate an apple.",
            collocation = "eat an apple",
            relatedWords = "fruit",
            note = "Simple word",
            level = "A1",
            nextReviewAt = 5000L,
            lastReviewedAt = 4000L,
            reviewCount = 5,
            correctCount = 4,
            createdAt = 1000L,
            updatedAt = 2000L
        )
        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.deckId, domain.deckId)
        assertEquals(entity.word, domain.word)
        assertEquals(entity.pronunciation, domain.pronunciation)
        assertEquals(entity.pronunciationUk, domain.pronunciationUk)
        assertEquals(entity.pronunciationUs, domain.pronunciationUs)
        assertEquals(entity.pronunciationUkAudioUrl, domain.pronunciationUkAudioUrl)
        assertEquals(entity.pronunciationUsAudioUrl, domain.pronunciationUsAudioUrl)
        assertEquals(entity.pronunciationAudioUrl, domain.pronunciationAudioUrl)
        assertEquals(entity.meaning, domain.meaning)
        assertEquals(entity.description, domain.description)
        assertEquals(entity.example, domain.example)
        assertEquals(entity.collocation, domain.collocation)
        assertEquals(entity.relatedWords, domain.relatedWords)
        assertEquals(entity.note, domain.note)
        assertEquals(entity.level, domain.level)
        assertEquals(entity.nextReviewAt, domain.nextReviewAt)
        assertEquals(entity.lastReviewedAt, domain.lastReviewedAt)
        assertEquals(entity.reviewCount, domain.reviewCount)
        assertEquals(entity.correctCount, domain.correctCount)
        assertEquals(entity.createdAt, domain.createdAt)
        assertEquals(entity.updatedAt, domain.updatedAt)
    }

    @Test
    fun word_toEntity_mapsCorrectly() {
        val domain = Word(
            id = 100,
            deckId = 5,
            word = "apple",
            pronunciation = "/ˈæpl/",
            meaning = "quả táo",
            description = "A round fruit",
            example = "He ate an apple.",
            collocation = "eat an apple",
            relatedWords = "fruit",
            note = "Simple word",
            level = "A1",
            nextReviewAt = 5000L,
            lastReviewedAt = 4000L,
            reviewCount = 5,
            correctCount = 4,
            createdAt = 1000L,
            updatedAt = 2000L
        )
        val entity = domain.toEntity()

        assertEquals(domain.id, entity.id)
        assertEquals(domain.deckId, entity.deckId)
        assertEquals(domain.word, entity.word)
        assertEquals(domain.pronunciation, entity.pronunciation)
        assertEquals(domain.pronunciationUk, entity.pronunciationUk)
        assertEquals(domain.pronunciationUs, entity.pronunciationUs)
        assertEquals(domain.pronunciationUkAudioUrl, entity.pronunciationUkAudioUrl)
        assertEquals(domain.pronunciationUsAudioUrl, entity.pronunciationUsAudioUrl)
        assertEquals(domain.pronunciationAudioUrl, entity.pronunciationAudioUrl)
        assertEquals(domain.meaning, entity.meaning)
        assertEquals(domain.description, entity.description)
        assertEquals(domain.example, entity.example)
        assertEquals(domain.collocation, entity.collocation)
        assertEquals(domain.relatedWords, entity.relatedWords)
        assertEquals(domain.note, entity.note)
        assertEquals(domain.level, entity.level)
        assertEquals(domain.nextReviewAt, entity.nextReviewAt)
        assertEquals(domain.lastReviewedAt, entity.lastReviewedAt)
        assertEquals(domain.reviewCount, entity.reviewCount)
        assertEquals(domain.correctCount, entity.correctCount)
        assertEquals(domain.createdAt, entity.createdAt)
        assertEquals(domain.updatedAt, entity.updatedAt)
    }

    @Test
    fun reviewHistoryEntity_toDomain_mapsCorrectly() {
        val entity = ReviewHistoryEntity(
            id = 1000,
            wordId = 50,
            deckId = 5,
            result = "GOOD",
            reviewedAt = 99999L
        )
        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.wordId, domain.wordId)
        assertEquals(entity.deckId, domain.deckId)
        assertEquals(entity.result, domain.result)
        assertEquals(entity.reviewedAt, domain.reviewedAt)
    }

    @Test
    fun reviewHistory_toEntity_mapsCorrectly() {
        val domain = ReviewHistory(
            id = 1000,
            wordId = 50,
            deckId = 5,
            result = "GOOD",
            reviewedAt = 99999L
        )
        val entity = domain.toEntity()

        assertEquals(domain.id, entity.id)
        assertEquals(domain.wordId, entity.wordId)
        assertEquals(domain.deckId, entity.deckId)
        assertEquals(domain.result, entity.result)
        assertEquals(domain.reviewedAt, entity.reviewedAt)
    }
}
