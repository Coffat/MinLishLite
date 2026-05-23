package com.example.minlishlite.data.mapper

import com.example.minlishlite.data.remote.dto.DictionaryEntryDto
import com.example.minlishlite.data.remote.dto.PhoneticDto

private enum class PhoneticRegion {
    UK,
    US,
    UNKNOWN
}

private data class ParsedPhonetic(
    val text: String,
    val audioUrl: String,
    val region: PhoneticRegion
)

fun DictionaryEntryDto.extractPronunciationVariants(): PronunciationVariants {
    val parsed = phonetics.orEmpty()
        .mapNotNull { it.toParsedPhonetic() }
        .distinctBy { "${it.text}|${it.audioUrl}|${it.region}" }

    var uk = parsed.firstOrNull { it.region == PhoneticRegion.UK }
    var us = parsed.firstOrNull { it.region == PhoneticRegion.US }

    val unknowns = parsed.filter { it.region == PhoneticRegion.UNKNOWN }
    if (uk == null && unknowns.isNotEmpty()) {
        uk = unknowns.first()
    }
    if (us == null) {
        us = unknowns.firstOrNull { it != uk }
            ?: parsed.firstOrNull { it != uk && it.region != PhoneticRegion.UK }
    }

    val fallbackText = phonetic?.trim().orEmpty()
    val ukText = uk?.text?.takeIf { it.isNotBlank() } ?: fallbackText
    val usText = us?.text?.takeIf { it.isNotBlank() }.orEmpty()

    return PronunciationVariants(
        ukText = ukText,
        ukAudioUrl = uk?.audioUrl.orEmpty(),
        usText = if (usText.isNotBlank() && usText != ukText) usText else us?.text.orEmpty(),
        usAudioUrl = us?.audioUrl.orEmpty()
    )
}

private fun PhoneticDto.toParsedPhonetic(): ParsedPhonetic? {
    val text = text?.trim().orEmpty()
    val audioUrl = audio?.trim()?.let(::normalizeAudioUrl).orEmpty()
    if (text.isBlank() && audioUrl.isBlank()) return null

    return ParsedPhonetic(
        text = text,
        audioUrl = audioUrl,
        region = detectRegion(audioUrl)
    )
}

private fun detectRegion(audioUrl: String): PhoneticRegion {
    if (audioUrl.isBlank()) return PhoneticRegion.UNKNOWN

    val lower = audioUrl.lowercase()
    return when {
        "-uk" in lower || "/uk/" in lower || "-gb" in lower || "british" in lower -> PhoneticRegion.UK
        "-us" in lower || "/us/" in lower || "american" in lower || "en-us" in lower -> PhoneticRegion.US
        else -> PhoneticRegion.UNKNOWN
    }
}

private fun normalizeAudioUrl(url: String): String {
    return when {
        url.startsWith("https://") || url.startsWith("http://") -> url
        url.startsWith("//") -> "https:$url"
        else -> url
    }
}
