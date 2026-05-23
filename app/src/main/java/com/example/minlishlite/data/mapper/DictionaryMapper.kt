package com.example.minlishlite.data.mapper

import com.example.minlishlite.data.remote.dto.DictionaryEntryDto
import com.example.minlishlite.data.remote.dto.MeaningDto
import com.example.minlishlite.domain.model.Word

fun DictionaryEntryDto.toDictionaryEntryData(queryWord: String): DictionaryEntryData {
    val trimmedWord = queryWord.trim()
    val allDefinitions = meanings.orEmpty().flatMap { meaning ->
        meaning.definitions.orEmpty().map { definition ->
            meaning to definition
        }
    }

    require(allDefinitions.isNotEmpty()) { "No definitions found for word: $trimmedWord" }

    val englishDefinitions = allDefinitions.map { (meaning, definition) ->
        EnglishDefinitionLine(
            partOfSpeech = meaning.partOfSpeech.orEmpty().trim(),
            text = definition.definition.orEmpty().trim()
        )
    }.filter { it.text.isNotBlank() }

    require(englishDefinitions.isNotEmpty()) { "No definitions found for word: $trimmedWord" }

    val example = allDefinitions
        .map { it.second.example }
        .firstOrNull { !it.isNullOrBlank() }
        .orEmpty()
        .trim()

    val pronunciations = extractPronunciationVariants()

    return DictionaryEntryData(
        word = trimmedWord,
        pronunciationUk = pronunciations.ukText,
        pronunciationUs = pronunciations.usText,
        pronunciationUkAudioUrl = pronunciations.ukAudioUrl,
        pronunciationUsAudioUrl = pronunciations.usAudioUrl,
        englishDefinitions = englishDefinitions,
        example = example,
        relatedWords = buildRelatedWords(meanings.orEmpty()),
        primaryPartOfSpeech = englishDefinitions.first().partOfSpeech
    )
}

fun DictionaryEntryData.toLookupWord(): Word {
    val primaryDefinition = englishDefinitions.first().text

    return Word(
        deckId = 0,
        word = word,
        pronunciation = buildCombinedPronunciation(pronunciationUk, pronunciationUs),
        pronunciationUk = pronunciationUk,
        pronunciationUs = pronunciationUs,
        pronunciationUkAudioUrl = pronunciationUkAudioUrl,
        pronunciationUsAudioUrl = pronunciationUsAudioUrl,
        pronunciationAudioUrl = pronunciationUkAudioUrl,
        meaning = primaryDefinition,
        description = buildEnglishDescription(),
        example = example,
        collocation = "",
        relatedWords = relatedWords,
        note = primaryPartOfSpeech,
        level = "Beginner",
        nextReviewAt = 0,
        createdAt = 0,
        updatedAt = 0
    )
}

@Deprecated("Use toDictionaryEntryData for lookup pipeline")
fun DictionaryEntryDto.toLookupWord(queryWord: String): Word {
    return toDictionaryEntryData(queryWord).toLookupWord()
}

private fun DictionaryEntryData.buildEnglishDescription(): String {
    if (englishDefinitions.size <= 1) {
        return ""
    }

    return englishDefinitions.drop(1).joinToString("\n") { line ->
        if (line.partOfSpeech.isNotBlank()) {
            "[${line.partOfSpeech}] ${line.text}"
        } else {
            line.text
        }
    }
}

private fun buildRelatedWords(meanings: List<MeaningDto>): String {
    val synonyms = linkedSetOf<String>()

    meanings.forEach { meaning ->
        meaning.synonyms.orEmpty().forEach { synonym ->
            if (synonym.isNotBlank()) synonyms.add(synonym.trim())
        }
        meaning.definitions.orEmpty().forEach { definition ->
            definition.synonyms.orEmpty().forEach { synonym ->
                if (synonym.isNotBlank()) synonyms.add(synonym.trim())
            }
        }
    }

    return synonyms.joinToString(", ")
}
