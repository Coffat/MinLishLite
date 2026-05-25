package com.example.minlishlite.core.util

data class ResolvedPronunciations(
    val ukText: String,
    val usText: String,
    val ukAudioUrl: String,
    val usAudioUrl: String
)

object PronunciationHelper {
    fun sanitizePhoneticInput(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""
        var result = trimmed
        if (result.startsWith("/")) result = result.substring(1)
        if (result.endsWith("/")) result = result.substring(0, result.length - 1)
        return result.trim()
    }

    fun buildCombinedPronunciation(uk: String, us: String): String {
        val cleanUk = sanitizePhoneticInput(uk)
        val cleanUs = sanitizePhoneticInput(us)
        return when {
            cleanUk.isNotEmpty() && cleanUs.isNotEmpty() -> "UK /$cleanUk/ • US /$cleanUs/"
            cleanUk.isNotEmpty() -> "UK /$cleanUk/"
            cleanUs.isNotEmpty() -> "US /$cleanUs/"
            else -> ""
        }
    }

    fun resolvePronunciationFields(
        pronunciation: String,
        pronunciationUk: String,
        pronunciationUs: String,
        pronunciationUkAudioUrl: String,
        pronunciationUsAudioUrl: String,
        pronunciationAudioUrl: String
    ): ResolvedPronunciations {
        return ResolvedPronunciations(
            ukText = pronunciationUk.ifEmpty { pronunciation },
            usText = pronunciationUs.ifEmpty { pronunciation },
            ukAudioUrl = pronunciationUkAudioUrl.ifEmpty { pronunciationAudioUrl },
            usAudioUrl = pronunciationUsAudioUrl.ifEmpty { pronunciationAudioUrl }
        )
    }
}
