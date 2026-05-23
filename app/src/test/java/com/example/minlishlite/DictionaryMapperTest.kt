package com.example.minlishlite

import com.example.minlishlite.data.mapper.toDictionaryEntryData
import com.example.minlishlite.data.remote.dto.DefinitionDto
import com.example.minlishlite.data.remote.dto.DictionaryEntryDto
import com.example.minlishlite.data.remote.dto.MeaningDto
import com.example.minlishlite.data.remote.dto.PhoneticDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryMapperTest {

    @Test
    fun toDictionaryEntryData_mapsUkAndUsPronunciations() {
        val entry = DictionaryEntryDto(
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
                            example = "Hello, how are you?",
                            synonyms = listOf("hi")
                        )
                    ),
                    synonyms = listOf("greetings")
                )
            )
        )

        val result = entry.toDictionaryEntryData("hello")

        assertEquals("hello", result.word)
        assertEquals("/həˈləʊ/", result.pronunciationUk)
        assertEquals("/həˈloʊ/", result.pronunciationUs)
        assertTrue(result.pronunciationUkAudioUrl.contains("hello-uk.mp3"))
        assertTrue(result.pronunciationUsAudioUrl.contains("hello-us.mp3"))
        assertEquals("Used as a greeting.", result.englishDefinitions.first().text)
        assertEquals("Hello, how are you?", result.example)
    }

    @Test
    fun toDictionaryEntryData_usesPhoneticsFallbackWhenPhoneticMissing() {
        val entry = DictionaryEntryDto(
            word = "study",
            phonetic = null,
            phonetics = listOf(PhoneticDto(text = "/ˈstʌdi/")),
            meanings = listOf(
                MeaningDto(
                    partOfSpeech = "verb",
                    definitions = listOf(
                        DefinitionDto(definition = "To learn about a subject.")
                    )
                )
            )
        )

        val result = entry.toDictionaryEntryData("study")

        assertEquals("/ˈstʌdi/", result.pronunciationUk)
        assertEquals("To learn about a subject.", result.englishDefinitions.first().text)
    }

    @Test(expected = IllegalArgumentException::class)
    fun toDictionaryEntryData_whenNoDefinitions_throws() {
        val entry = DictionaryEntryDto(
            word = "unknown",
            phonetic = null,
            phonetics = emptyList(),
            meanings = emptyList()
        )

        entry.toDictionaryEntryData("unknown")
    }
}
