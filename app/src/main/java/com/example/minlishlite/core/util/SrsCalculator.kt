package com.example.minlishlite.core.util

import com.example.minlishlite.data.model.ReviewResult
import java.util.concurrent.TimeUnit

data class SrsReviewOutcome(
    val nextReviewAt: Long,
    val easeFactor: Float,
    val isCorrect: Boolean
)

object SrsCalculator {

    const val DEFAULT_EASE_FACTOR = 2.5f
    private const val MIN_EASE_FACTOR = 1.3f

    fun calculateNextReview(result: ReviewResult, now: Long): Long {
        return when (result) {
            ReviewResult.AGAIN -> now
            ReviewResult.HARD -> now + TimeUnit.DAYS.toMillis(1)
            ReviewResult.GOOD -> now + TimeUnit.DAYS.toMillis(3)
            ReviewResult.EASY -> now + TimeUnit.DAYS.toMillis(7)
        }
    }

    fun adjustEaseFactor(currentEaseFactor: Float, result: ReviewResult): Float {
        val adjusted = when (result) {
            ReviewResult.AGAIN -> currentEaseFactor - 0.2f
            ReviewResult.HARD -> currentEaseFactor - 0.1f
            ReviewResult.GOOD -> currentEaseFactor
            ReviewResult.EASY -> currentEaseFactor + 0.15f
        }
        return adjusted.coerceAtLeast(MIN_EASE_FACTOR)
    }

    fun applyReview(
        currentEaseFactor: Float,
        result: ReviewResult,
        now: Long
    ): SrsReviewOutcome {
        val easeFactor = adjustEaseFactor(currentEaseFactor, result)
        return SrsReviewOutcome(
            nextReviewAt = calculateNextReview(result, now),
            easeFactor = easeFactor,
            isCorrect = result != ReviewResult.AGAIN
        )
    }
}
