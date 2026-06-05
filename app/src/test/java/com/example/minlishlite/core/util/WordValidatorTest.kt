package com.example.minlishlite.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordValidatorTest {

    @Test
    fun `validateWord returns true for valid inputs`() {
        assertTrue(WordValidator.validateWord("hello", "xin chào"))
    }

    @Test
    fun `validateWord returns false when word is blank`() {
        assertFalse(WordValidator.validateWord("", "xin chào"))
        assertFalse(WordValidator.validateWord("   ", "xin chào"))
    }

    @Test
    fun `validateWord returns false when meaning is blank`() {
        assertFalse(WordValidator.validateWord("hello", ""))
        assertFalse(WordValidator.validateWord("hello", "   "))
    }
}
