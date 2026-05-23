package com.example.minlishlite

import com.example.minlishlite.data.mapper.parseCombinedPronunciation
import com.example.minlishlite.data.mapper.resolvePronunciationFields
import com.example.minlishlite.data.mapper.sanitizePhoneticInput
import org.junit.Assert.assertEquals
import org.junit.Test

class PronunciationVariantsTest {

    @Test
    fun parseCombinedPronunciation_splitsUkAndUsLines() {
        val parsed = parseCombinedPronunciation(
            "Anh-Anh: /θroʊ/\nAnh-Mỹ: /θɹoʊ/"
        )

        assertEquals("/θroʊ/", parsed.ukText)
        assertEquals("/θɹoʊ/", parsed.usText)
    }

    @Test
    fun parseCombinedPronunciation_whenOnlyUsLine_doesNotAssignToUk() {
        val parsed = parseCombinedPronunciation("Anh-Mỹ: /θɹoʊ/")

        assertEquals("", parsed.ukText)
        assertEquals("/θɹoʊ/", parsed.usText)
    }

    @Test
    fun sanitizePhoneticInput_removesRegionPrefix() {
        assertEquals("/θɹoʊ/", sanitizePhoneticInput("Anh-Mỹ: /θɹoʊ/"))
        assertEquals("/θroʊ/", sanitizePhoneticInput("Anh-Anh: /θroʊ/"))
    }

    @Test
    fun resolvePronunciationFields_prefersExplicitUkUsFields() {
        val resolved = resolvePronunciationFields(
            pronunciation = "Anh-Mỹ: /θɹoʊ/",
            pronunciationUk = "/θroʊ/",
            pronunciationUs = "/θɹoʊ/"
        )

        assertEquals("/θroʊ/", resolved.ukText)
        assertEquals("/θɹoʊ/", resolved.usText)
    }

    @Test
    fun resolvePronunciationFields_parsesCombinedWhenUkUsBlank() {
        val resolved = resolvePronunciationFields(
            pronunciation = "Anh-Mỹ: /θɹoʊ/",
            pronunciationUk = "",
            pronunciationUs = ""
        )

        assertEquals("", resolved.ukText)
        assertEquals("/θɹoʊ/", resolved.usText)
    }
}
