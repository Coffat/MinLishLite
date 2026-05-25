package com.example.minlishlite.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordValidatorTest {

    @Test
    fun validateWord_validInputs_shouldReturnTrue() {
        val isValid = WordValidator.validateWord("hello", "xin chào")
        assertTrue(isValid)
    }

    @Test
    fun validateWord_emptyWord_shouldReturnFalse() {
        val isValid = WordValidator.validateWord("", "xin chào")
        assertFalse(isValid)
    }

    @Test
    fun validateWord_blankMeaning_shouldReturnFalse() {
        val isValid = WordValidator.validateWord("hello", "   ")
        assertFalse(isValid)
    }
}
