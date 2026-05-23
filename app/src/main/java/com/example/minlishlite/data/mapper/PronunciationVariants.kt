package com.example.minlishlite.data.mapper

data class PronunciationVariants(
    val ukText: String = "",
    val ukAudioUrl: String = "",
    val usText: String = "",
    val usAudioUrl: String = ""
)

fun buildCombinedPronunciation(ukText: String, usText: String): String {
    val uk = sanitizePhoneticInput(ukText)
    val us = sanitizePhoneticInput(usText)

    return listOfNotNull(
        uk.takeIf { it.isNotBlank() }?.let { "Anh-Anh: $it" },
        us.takeIf { it.isNotBlank() }?.let { "Anh-Mỹ: $it" }
    ).joinToString("\n")
}

fun sanitizePhoneticInput(value: String): String {
    return value.trim()
        .removePrefix("Anh-Anh:")
        .removePrefix("Anh-Mỹ:")
        .trim()
}

fun parseCombinedPronunciation(combined: String): PronunciationVariants {
    if (combined.isBlank()) {
        return PronunciationVariants()
    }

    var ukText = ""
    var usText = ""
    val plainLines = mutableListOf<String>()

    combined.lines().forEach { rawLine ->
        val line = rawLine.trim()
        if (line.isBlank()) return@forEach

        when {
            line.startsWith("Anh-Anh:", ignoreCase = true) -> {
                ukText = line.substringAfter(":").trim()
            }
            line.startsWith("Anh-Mỹ:", ignoreCase = true) -> {
                usText = line.substringAfter(":").trim()
            }
            else -> plainLines.add(line)
        }
    }

    if (ukText.isBlank() && usText.isBlank() && plainLines.size == 1) {
        ukText = plainLines.first()
    } else if (ukText.isBlank() && usText.isBlank()) {
        plainLines.forEachIndexed { index, line ->
            if (index == 0) ukText = line else if (index == 1) usText = line
        }
    }

    return PronunciationVariants(
        ukText = sanitizePhoneticInput(ukText),
        usText = sanitizePhoneticInput(usText)
    )
}

fun resolvePronunciationFields(
    pronunciation: String,
    pronunciationUk: String,
    pronunciationUs: String,
    pronunciationUkAudioUrl: String = "",
    pronunciationUsAudioUrl: String = "",
    pronunciationAudioUrl: String = ""
): PronunciationVariants {
    val parsedCombined = parseCombinedPronunciation(pronunciation)

    val ukText = sanitizePhoneticInput(pronunciationUk).ifBlank { parsedCombined.ukText }
    val usText = sanitizePhoneticInput(pronunciationUs).ifBlank { parsedCombined.usText }

    return PronunciationVariants(
        ukText = ukText,
        ukAudioUrl = pronunciationUkAudioUrl.ifBlank { pronunciationAudioUrl },
        usText = usText,
        usAudioUrl = pronunciationUsAudioUrl
    )
}
