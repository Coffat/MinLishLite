package com.example.minlishlite.data.remote.api

import com.example.minlishlite.data.remote.dto.DictionaryEntryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    suspend fun lookupWord(@Path("word") word: String): List<DictionaryEntryDto>
}
