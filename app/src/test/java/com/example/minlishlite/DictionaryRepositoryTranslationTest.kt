package com.example.minlishlite

import com.example.minlishlite.data.mapper.toVietnamesePartOfSpeech
import com.example.minlishlite.data.remote.dto.DefinitionDto
import com.example.minlishlite.data.remote.dto.DictionaryEntryDto
import com.example.minlishlite.data.remote.dto.MeaningDto
import com.example.minlishlite.data.remote.dto.PhoneticDto
import com.example.minlishlite.data.repository.DictionaryRepositoryImpl
import com.example.minlishlite.domain.repository.TranslationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryRepositoryTranslationTest {

    @Test
    fun lookupWord_translatesMeaningAndDescriptionSeparately() = runTest {
        val fakeDictionaryApi = FakeDictionaryApiService(
            entries = listOf(
                DictionaryEntryDto(
                    word = "hello",
                    phonetic = "/həˈləʊ/",
                    phonetics = listOf(
                        PhoneticDto(
                            text = "/həˈləʊ/",
                            audio = "https://api.dictionaryapi.dev/media/pronunciations/en/hello-uk.mp3"
                        ),
                        PhoneticDto(
                            text = "/həˈloʊ/",
                            audio = "https://api.dictionaryapi.dev/media/pronunciations/en/hello-us.mp3"
                        )
                    ),
                    meanings = listOf(
                        MeaningDto(
                            partOfSpeech = "noun",
                            definitions = listOf(
                                DefinitionDto(
                                    definition = "Used as a greeting.",
                                    example = "Hello, how are you?"
                                )
                            )
                        )
                    )
                )
            )
        )
        val fakeTranslationRepository = FakeTranslationRepository(
            translations = mapOf(
                "hello" to "xin chào",
                "Used as a greeting." to "Được dùng như lời chào."
            )
        )

        val repository = DictionaryRepositoryImpl(fakeDictionaryApi, fakeTranslationRepository)
        val result = repository.lookupWord("hello")

        assertTrue(result.isSuccess)
        val word = result.getOrThrow()
        assertEquals("xin chào", word.meaning)
        assertEquals("Được dùng như lời chào.", word.description)
        assertEquals("/həˈləʊ/", word.pronunciationUk)
        assertEquals("/həˈloʊ/", word.pronunciationUs)
        assertTrue(word.pronunciationUkAudioUrl.contains("hello-uk.mp3"))
        assertTrue(word.pronunciationUsAudioUrl.contains("hello-us.mp3"))
        assertEquals("danh từ", word.note)
    }

    @Test
    fun partOfSpeechMapper_mapsCommonValuesToVietnamese() {
        assertEquals("danh từ", "noun".toVietnamesePartOfSpeech())
        assertEquals("động từ", "verb".toVietnamesePartOfSpeech())
        assertEquals("tính từ", "adjective".toVietnamesePartOfSpeech())
    }

    private class FakeDictionaryApiService(
        private val entries: List<DictionaryEntryDto>
    ) : com.example.minlishlite.data.remote.api.DictionaryApiService {
        override suspend fun lookupWord(word: String): List<DictionaryEntryDto> = entries
    }

    private class FakeTranslationRepository(
        private val translations: Map<String, String>
    ) : TranslationRepository {
        override suspend fun translateEnToVi(text: String): Result<String> {
            val translated = translations[text.trim()]
            return if (translated != null) {
                Result.success(translated)
            } else {
                Result.success("Dịch: $text")
            }
        }
    }
}
