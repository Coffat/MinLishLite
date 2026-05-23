package com.example.minlishlite.data.mapper

import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import com.example.minlishlite.data.local.entity.UserEntity
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.domain.model.ReviewHistory
import com.example.minlishlite.domain.model.User
import com.example.minlishlite.domain.model.Word

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    goal = goal,
    level = level,
    createdAt = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    goal = goal,
    level = level,
    createdAt = createdAt
)

fun DeckEntity.toDomain(): Deck = Deck(
    id = id,
    name = name,
    description = description,
    tag = tag,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Deck.toEntity(): DeckEntity = DeckEntity(
    id = id,
    name = name,
    description = description,
    tag = tag,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun WordEntity.toDomain(): Word = Word(
    id = id,
    deckId = deckId,
    word = word,
    pronunciation = pronunciation,
    pronunciationUk = pronunciationUk,
    pronunciationUs = pronunciationUs,
    pronunciationUkAudioUrl = pronunciationUkAudioUrl,
    pronunciationUsAudioUrl = pronunciationUsAudioUrl,
    pronunciationAudioUrl = pronunciationAudioUrl,
    meaning = meaning,
    description = description,
    example = example,
    collocation = collocation,
    relatedWords = relatedWords,
    note = note,
    level = level,
    easeFactor = easeFactor,
    nextReviewAt = nextReviewAt,
    lastReviewedAt = lastReviewedAt,
    reviewCount = reviewCount,
    correctCount = correctCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Word.toEntity(): WordEntity = WordEntity(
    id = id,
    deckId = deckId,
    word = word,
    pronunciation = pronunciation,
    pronunciationUk = pronunciationUk,
    pronunciationUs = pronunciationUs,
    pronunciationUkAudioUrl = pronunciationUkAudioUrl,
    pronunciationUsAudioUrl = pronunciationUsAudioUrl,
    pronunciationAudioUrl = pronunciationAudioUrl,
    meaning = meaning,
    description = description,
    example = example,
    collocation = collocation,
    relatedWords = relatedWords,
    note = note,
    level = level,
    easeFactor = easeFactor,
    nextReviewAt = nextReviewAt,
    lastReviewedAt = lastReviewedAt,
    reviewCount = reviewCount,
    correctCount = correctCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ReviewHistoryEntity.toDomain(): ReviewHistory = ReviewHistory(
    id = id,
    wordId = wordId,
    deckId = deckId,
    result = result,
    reviewedAt = reviewedAt
)

fun ReviewHistory.toEntity(): ReviewHistoryEntity = ReviewHistoryEntity(
    id = id,
    wordId = wordId,
    deckId = deckId,
    result = result,
    reviewedAt = reviewedAt
)
