package com.example.minlishlite.data.repository

import com.example.minlishlite.core.result.NetworkException
import com.example.minlishlite.data.remote.api.TranslationApiService
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class TranslationRepository(
    private val apiService: TranslationApiService
) {

    suspend fun translateEnToVi(text: String): Result<String> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) {
            return@withContext Result.success("")
        }

        try {
            val requestText = trimmed.take(MAX_CHARS)
            val response = apiService.translate(text = requestText)
            val translated = response.responseData?.translatedText?.trim().orEmpty()

            if (translated.isBlank()) {
                Result.failure(IllegalStateException("Empty translation response"))
            } else {
                Result.success(translated)
            }
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(NetworkException(e))
        }
    }

    companion object {
        private const val MAX_CHARS = 450
    }
}
