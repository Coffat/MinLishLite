package com.example.minlishlite.data.repository

import com.example.minlishlite.core.result.NetworkException
import com.example.minlishlite.core.result.WordNotFoundException
import com.example.minlishlite.data.mapper.buildCombinedPronunciation
import com.example.minlishlite.data.mapper.DictionaryEntryData
import com.example.minlishlite.data.mapper.EnglishDefinitionLine
import com.example.minlishlite.data.mapper.toDictionaryEntryData
import com.example.minlishlite.data.mapper.toVietnamesePartOfSpeech
import com.example.minlishlite.data.remote.api.DictionaryApiService
import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.domain.repository.DictionaryRepository
import com.example.minlishlite.domain.repository.TranslationRepository
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class DictionaryRepositoryImpl(
    private val apiService: DictionaryApiService,
    private val translationRepository: TranslationRepository
) : DictionaryRepository {

    override suspend fun lookupWord(word: String): Result<Word> = withContext(Dispatchers.IO) {
        if (word.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Word cannot be blank"))
        }

        try {
            val response = apiService.lookupWord(word.trim())
            if (response.isEmpty()) {
                return@withContext Result.failure(WordNotFoundException())
            }

            val entryData = response.first().toDictionaryEntryData(word)
            Result.success(entryData.toVietnameseWord())
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> Result.failure(WordNotFoundException())
                else -> Result.failure(e)
            }
        } catch (e: IOException) {
            Result.failure(NetworkException(e))
        } catch (e: IllegalArgumentException) {
            Result.failure(WordNotFoundException())
        }
    }

    private suspend fun DictionaryEntryData.toVietnameseWord(): Word {
        val vietnameseMeaning = translationRepository.translateEnToVi(word)
            .getOrElse {
                val fallback = englishDefinitions.firstOrNull()?.text.orEmpty()
                if (fallback.isNotBlank()) {
                    translationRepository.translateEnToVi(fallback).getOrDefault(fallback)
                } else {
                    word
                }
            }

        val vietnameseDescription = buildVietnameseDescription(englishDefinitions)

        return Word(
            deckId = 0,
            word = word,
            pronunciation = buildCombinedPronunciation(pronunciationUk, pronunciationUs),
            pronunciationUk = pronunciationUk,
            pronunciationUs = pronunciationUs,
            pronunciationUkAudioUrl = pronunciationUkAudioUrl,
            pronunciationUsAudioUrl = pronunciationUsAudioUrl,
            pronunciationAudioUrl = pronunciationUkAudioUrl,
            meaning = vietnameseMeaning,
            description = vietnameseDescription,
            example = example,
            collocation = "",
            relatedWords = relatedWords,
            note = primaryPartOfSpeech.toVietnamesePartOfSpeech(),
            level = "Beginner",
            nextReviewAt = 0,
            createdAt = 0,
            updatedAt = 0
        )
    }

    private suspend fun buildVietnameseDescription(
        definitions: List<EnglishDefinitionLine>
    ): String {
        if (definitions.isEmpty()) return ""

        val translatedLines = definitions.mapNotNull { line ->
            val sourceText = line.text.trim()
            if (sourceText.isEmpty()) return@mapNotNull null

            val translated = translationRepository.translateEnToVi(sourceText).getOrDefault(sourceText)
            val partOfSpeech = line.partOfSpeech.toVietnamesePartOfSpeech()

            when {
                definitions.size == 1 -> translated
                partOfSpeech.isNotBlank() -> "• [$partOfSpeech] $translated"
                else -> "• $translated"
            }
        }

        return translatedLines.joinToString("\n")
    }
}
