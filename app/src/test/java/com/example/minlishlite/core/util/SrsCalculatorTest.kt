package com.example.minlishlite.core.util

import com.example.minlishlite.data.model.ReviewResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class SrsCalculatorTest {

    @Test
    fun `calculateNextReview returns correct interval for AGAIN`() {
        val now = 1000L
        val nextReview = SrsCalculator.calculateNextReview(ReviewResult.AGAIN, now)
        assertEquals(now, nextReview)
    }

    @Test
    fun `calculateNextReview returns correct interval for HARD`() {
        val now = 1000L
        val expected = now + TimeUnit.DAYS.toMillis(1)
        val nextReview = SrsCalculator.calculateNextReview(ReviewResult.HARD, now)
        assertEquals(expected, nextReview)
    }

    @Test
    fun `calculateNextReview returns correct interval for GOOD`() {
        val now = 1000L
        val expected = now + TimeUnit.DAYS.toMillis(3)
        val nextReview = SrsCalculator.calculateNextReview(ReviewResult.GOOD, now)
        assertEquals(expected, nextReview)
    }

    @Test
    fun `calculateNextReview returns correct interval for EASY`() {
        val now = 1000L
        val expected = now + TimeUnit.DAYS.toMillis(7)
        val nextReview = SrsCalculator.calculateNextReview(ReviewResult.EASY, now)
        assertEquals(expected, nextReview)
    }

    @Test
    fun `adjustEaseFactor decreases by 0_2 for AGAIN and respects min ease`() {
        val ease1 = SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.AGAIN)
        assertEquals(2.3f, ease1, 0.01f)

        val ease2 = SrsCalculator.adjustEaseFactor(1.4f, ReviewResult.AGAIN)
        assertEquals(1.3f, ease2, 0.01f) // Should coerce at least 1.3f
    }

    @Test
    fun `adjustEaseFactor decreases by 0_1 for HARD`() {
        val ease = SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.HARD)
        assertEquals(2.4f, ease, 0.01f)
    }

    @Test
    fun `adjustEaseFactor remains same for GOOD`() {
        val ease = SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.GOOD)
        assertEquals(2.5f, ease, 0.01f)
    }

    @Test
    fun `adjustEaseFactor increases by 0_15 for EASY`() {
        val ease = SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.EASY)
        assertEquals(2.65f, ease, 0.01f)
    }

    @Test
    fun `applyReview returns correct outcome`() {
        val now = 1000L
        val outcome = SrsCalculator.applyReview(2.5f, ReviewResult.GOOD, now)
        
        assertEquals(now + TimeUnit.DAYS.toMillis(3), outcome.nextReviewAt)
        assertEquals(2.5f, outcome.easeFactor, 0.01f)
        assertTrue(outcome.isCorrect)
    }

    @Test
    fun `applyReview sets isCorrect to false for AGAIN`() {
        val outcome = SrsCalculator.applyReview(2.5f, ReviewResult.AGAIN, 1000L)
        assertFalse(outcome.isCorrect)
    }
}
