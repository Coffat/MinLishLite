package com.example.minlishlite.core.util

import com.example.minlishlite.data.model.ReviewResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class SrsCalculatorTest {

    @Test
    fun calculateNextReview_again_shouldReturnNow() {
        val now = 1000000L
        val nextReview = SrsCalculator.calculateNextReview(ReviewResult.AGAIN, now)
        assertEquals(now, nextReview)
    }

    @Test
    fun calculateNextReview_good_shouldReturnThreeDaysLater() {
        val now = 1000000L
        val nextReview = SrsCalculator.calculateNextReview(ReviewResult.GOOD, now)
        val expected = now + TimeUnit.DAYS.toMillis(3)
        assertEquals(expected, nextReview)
    }

    @Test
    fun adjustEaseFactor_easy_shouldIncreaseEaseFactor() {
        val currentEase = 2.5f
        val newEase = SrsCalculator.adjustEaseFactor(currentEase, ReviewResult.EASY)
        assertTrue(newEase > currentEase)
        assertEquals(2.65f, newEase, 0.01f)
    }

    @Test
    fun adjustEaseFactor_again_shouldDecreaseEaseFactor() {
        val currentEase = 2.5f
        val newEase = SrsCalculator.adjustEaseFactor(currentEase, ReviewResult.AGAIN)
        assertTrue(newEase < currentEase)
        assertEquals(2.3f, newEase, 0.01f)
    }

    @Test
    fun applyReview_again_shouldMarkNotCorrect() {
        val now = 1000000L
        val outcome = SrsCalculator.applyReview(2.5f, ReviewResult.AGAIN, now)
        assertEquals(false, outcome.isCorrect)
    }
}
