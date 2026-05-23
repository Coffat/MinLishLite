package com.example.minlishlite.data.remote.api

import com.example.minlishlite.data.remote.dto.TranslationResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationApiService {
    @GET("get")
    suspend fun translate(
        @Query("q") text: String,
        @Query("langpair") langPair: String = "en|vi"
    ): TranslationResponseDto
}
