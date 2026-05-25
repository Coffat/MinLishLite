package com.example.minlishlite.core.util

import com.example.minlishlite.data.local.entity.WordEntity

object CsvHelper {

    private const val HEADER = "word,pronunciation,meaning,description,example,collocation,relatedWords,note,level"

    /**
     * Parses a CSV string content into a list of WordEntity objects for a given deck.
     */
    fun parseCsv(csvContent: String, deckId: Int): List<WordEntity> {
        val words = mutableListOf<WordEntity>()
        val lines = csvContent.lines()
        if (lines.isEmpty()) return words

        // Check if first line is a header
        val startRow = if (lines.first().lowercase().contains("meaning") || lines.first().lowercase().contains("word")) {
            1
        } else {
            0
        }

        val currentTime = System.currentTimeMillis()

        for (i in startRow until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue

            val fields = parseCsvLine(line)
            if (fields.isEmpty()) continue

            // Ensure we have at least word and meaning
            val word = fields.getOrNull(0) ?: continue
            val meaning = fields.getOrNull(2) ?: continue
            if (word.isBlank() || meaning.isBlank()) continue

            val pronunciation = fields.getOrNull(1) ?: ""
            val description = fields.getOrNull(3) ?: ""
            val example = fields.getOrNull(4) ?: ""
            val collocation = fields.getOrNull(5) ?: ""
            val relatedWords = fields.getOrNull(6) ?: ""
            val note = fields.getOrNull(7) ?: ""
            val level = fields.getOrNull(8) ?: "B1"

            val wordEntity = WordEntity(
                deckId = deckId,
                word = word,
                pronunciation = pronunciation,
                meaning = meaning,
                description = description,
                example = example,
                collocation = collocation,
                relatedWords = relatedWords,
                note = note,
                level = level,
                nextReviewAt = currentTime,
                createdAt = currentTime,
                updatedAt = currentTime
            )
            words.add(wordEntity)
        }
        return words
    }

    /**
     * Exports a list of WordEntity objects into a CSV string.
     */
    fun exportToCsv(words: List<WordEntity>): String {
        val builder = java.lang.StringBuilder()
        builder.append(HEADER).append("\n")

        for (word in words) {
            val line = listOf(
                word.word,
                word.pronunciation,
                word.meaning,
                word.description,
                word.example,
                word.collocation,
                word.relatedWords,
                word.note,
                word.level
            ).joinToString(",") { escapeCsvField(it) }
            builder.append(line).append("\n")
        }
        return builder.toString()
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = java.lang.StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val char = line[i]
            if (char == '"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    // Double quotes inside quotes means single double quote character
                    current.append('"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (char == ',' && !inQuotes) {
                result.add(current.toString().trim())
                current.setLength(0)
            } else {
                current.append(char)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }

    private fun escapeCsvField(field: String): String {
        val escaped = field.replace("\"", "\"\"")
        return if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            "\"$escaped\""
        } else {
            escaped
        }
    }
}
