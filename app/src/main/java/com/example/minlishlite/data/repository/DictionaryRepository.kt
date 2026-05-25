package com.example.minlishlite.data.repository

import com.example.minlishlite.core.result.NetworkException
import com.example.minlishlite.core.result.WordNotFoundException
import com.example.minlishlite.data.remote.api.DictionaryApiService
import com.example.minlishlite.data.model.DictionaryResult
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class DictionaryRepository(
    private val apiService: DictionaryApiService,
    private val translationRepository: TranslationRepository
) {

    suspend fun lookupWord(word: String): Result<DictionaryResult> = withContext(Dispatchers.IO) {
        val trimmed = word.trim()
        if (trimmed.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Word cannot be blank"))
        }

        try {
            val response = apiService.lookupWord(trimmed)
            if (response.isEmpty()) {
                return@withContext Result.failure(WordNotFoundException())
            }

            val entryDto = response.first()

            // Extract pronunciation details
            var pronunciationUk = ""
            var pronunciationUs = ""
            var audioUk = ""
            var audioUs = ""
            val phoneticsList = entryDto.phonetics ?: emptyList()
            for (p in phoneticsList) {
                val text = p.text.orEmpty()
                val audioUrl = p.audio.orEmpty()
                if (audioUrl.contains("-uk") || audioUrl.contains("/uk/")) {
                    audioUk = audioUrl
                    if (text.isNotEmpty()) pronunciationUk = text
                } else if (audioUrl.contains("-us") || audioUrl.contains("/us/")) {
                    audioUs = audioUrl
                    if (text.isNotEmpty()) pronunciationUs = text
                } else {
                    if (audioUk.isEmpty()) audioUk = audioUrl
                    if (pronunciationUk.isEmpty() && text.isNotEmpty()) pronunciationUk = text
                }
            }
            if (pronunciationUs.isEmpty()) pronunciationUs = pronunciationUk
            if (audioUs.isEmpty()) audioUs = audioUk
            if (pronunciationUk.isEmpty()) pronunciationUk = entryDto.phonetic.orEmpty()
            if (pronunciationUs.isEmpty()) pronunciationUs = entryDto.phonetic.orEmpty()

            // Extract meanings and examples
            val firstMeaning = entryDto.meanings?.firstOrNull()
            val firstDefinition = firstMeaning?.definitions?.firstOrNull()

            val englishDef = firstDefinition?.definition.orEmpty()
            val partOfSpeech = firstMeaning?.partOfSpeech.orEmpty()
            val rawExample = firstDefinition?.example.orEmpty()

            // Translate meaning (word translation)
            val translatedWord = translationRepository.translateEnToVi(trimmed).getOrDefault(trimmed)

            // Translate definition
            val translatedDef = if (englishDef.isNotBlank()) {
                translationRepository.translateEnToVi(englishDef).getOrDefault(englishDef)
            } else {
                ""
            }

            // Translate example
            val translatedExample = if (rawExample.isNotBlank()) {
                translationRepository.translateEnToVi(rawExample).getOrDefault(rawExample)
            } else {
                ""
            }

            Result.success(
                DictionaryResult(
                    word = trimmed,
                    pronunciationUk = pronunciationUk,
                    pronunciationUs = pronunciationUs,
                    pronunciationUkAudioUrl = audioUk,
                    pronunciationUsAudioUrl = audioUs,
                    meaning = translatedWord,
                    definition = translatedDef,
                    partOfSpeech = partOfSpeech,
                    example = translatedExample
                )
            )
        } catch (e: HttpException) {
            if (e.code() == 404) {
                Result.failure(WordNotFoundException())
            } else {
                Result.failure(e)
            }
        } catch (e: IOException) {
            Result.failure(NetworkException(e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
