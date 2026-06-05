package com.example.minlishlite.data.repository

import com.example.minlishlite.core.result.NetworkException
import com.example.minlishlite.core.result.WordNotFoundException
import com.example.minlishlite.data.model.DictionaryResult
import com.example.minlishlite.data.remote.api.DictionaryApiService
import com.example.minlishlite.data.remote.dto.DictionaryEntryDto
import com.example.minlishlite.data.remote.dto.MeaningDto
import com.example.minlishlite.data.remote.dto.DefinitionDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class DictionaryRepositoryTest {

    private lateinit var apiService: DictionaryApiService
    private lateinit var translationRepository: TranslationRepository
    private lateinit var dictionaryRepository: DictionaryRepository

    @Before
    fun setup() {
        apiService = mockk()
        translationRepository = mockk()
        dictionaryRepository = DictionaryRepository(apiService, translationRepository)
    }

    @Test
    fun lookupWord_blankWord_returnsFailure() = runTest {
        val result = dictionaryRepository.lookupWord("   ")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun lookupWord_apiReturnsEmpty_returnsWordNotFound() = runTest {
        coEvery { apiService.lookupWord("apple") } returns emptyList()
        val result = dictionaryRepository.lookupWord("apple")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is WordNotFoundException)
    }

    @Test
    fun lookupWord_apiThrowsIOException_returnsNetworkException() = runTest {
        coEvery { apiService.lookupWord("apple") } throws IOException()
        val result = dictionaryRepository.lookupWord("apple")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NetworkException)
    }

    @Test
    fun lookupWord_success_mapsDataCorrectly() = runTest {
        val dtos = listOf(
            DictionaryEntryDto(
                word = "apple",
                phonetic = "/ˈæp.əl/",
                phonetics = emptyList(),
                meanings = listOf(
                    MeaningDto(
                        partOfSpeech = "noun",
                        definitions = listOf(
                            DefinitionDto(
                                definition = "A fruit",
                                example = "I eat an apple"
                            )
                        )
                    )
                )
            )
        )
        coEvery { apiService.lookupWord("apple") } returns dtos
        coEvery { translationRepository.translateEnToVi("apple") } returns Result.success("Quả táo")
        coEvery { translationRepository.translateEnToVi("A fruit") } returns Result.success("Một loại quả")
        coEvery { translationRepository.translateEnToVi("I eat an apple") } returns Result.success("Tôi ăn một quả táo")

        val result = dictionaryRepository.lookupWord("apple")
        assertTrue(result.isSuccess)
        
        val dictResult = result.getOrNull()!!
        assertEquals("apple", dictResult.word)
        assertEquals("/ˈæp.əl/", dictResult.pronunciationUk)
        assertEquals("Quả táo", dictResult.meaning)
        assertEquals("Một loại quả", dictResult.definition)
        assertEquals("Tôi ăn một quả táo", dictResult.example)
        assertEquals("noun", dictResult.partOfSpeech)
    }
}
