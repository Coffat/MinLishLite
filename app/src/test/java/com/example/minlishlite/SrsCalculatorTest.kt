package com.example.minlishlite

import com.example.minlishlite.domain.model.ReviewResult
import com.example.minlishlite.domain.usecase.SrsCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class SrsCalculatorTest {

    private val now = 1_700_000_000_000L

    @Test
    fun again_shouldReturnToday() {
        val next = SrsCalculator.calculateNextReview(ReviewResult.AGAIN, now)
        assertEquals(now, next)
    }

    @Test
    fun hard_shouldReturnTomorrow() {
        val next = SrsCalculator.calculateNextReview(ReviewResult.HARD, now)
        assertEquals(now + TimeUnit.DAYS.toMillis(1), next)
    }

    @Test
    fun good_shouldReturnAfterThreeDays() {
        val next = SrsCalculator.calculateNextReview(ReviewResult.GOOD, now)
        assertEquals(now + TimeUnit.DAYS.toMillis(3), next)
    }

    @Test
    fun easy_shouldReturnAfterSevenDays() {
        val next = SrsCalculator.calculateNextReview(ReviewResult.EASY, now)
        assertEquals(now + TimeUnit.DAYS.toMillis(7), next)
    }

    @Test
    fun again_shouldDecreaseEaseFactor() {
        val adjusted = SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.AGAIN)
        assertEquals(2.3f, adjusted, 0.001f)
    }

    @Test
    fun hard_shouldDecreaseEaseFactor() {
        val adjusted = SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.HARD)
        assertEquals(2.4f, adjusted, 0.001f)
    }

    @Test
    fun good_shouldKeepEaseFactor() {
        val adjusted = SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.GOOD)
        assertEquals(2.5f, adjusted, 0.001f)
    }

    @Test
    fun easy_shouldIncreaseEaseFactor() {
        val adjusted = SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.EASY)
        assertEquals(2.65f, adjusted, 0.001f)
    }

    @Test
    fun easeFactor_shouldNotGoBelowMinimum() {
        val adjusted = SrsCalculator.adjustEaseFactor(1.35f, ReviewResult.AGAIN)
        assertEquals(1.3f, adjusted, 0.001f)
    }

    @Test
    fun applyReview_again_isNotCorrect() {
        val outcome = SrsCalculator.applyReview(2.5f, ReviewResult.AGAIN, now)
        assertFalse(outcome.isCorrect)
        assertEquals(now, outcome.nextReviewAt)
        assertEquals(2.3f, outcome.easeFactor, 0.001f)
    }

    @Test
    fun applyReview_good_isCorrect() {
        val outcome = SrsCalculator.applyReview(2.5f, ReviewResult.GOOD, now)
        assertTrue(outcome.isCorrect)
        assertEquals(now + TimeUnit.DAYS.toMillis(3), outcome.nextReviewAt)
    }
}
