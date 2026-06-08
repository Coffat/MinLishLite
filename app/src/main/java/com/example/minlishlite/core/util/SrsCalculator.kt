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
    fun calculateNextReview(result: ReviewResult, now: Long, easeFactor: Float): Long {
        // AGAIN = chưa nhớ → ôn lại ngay, không tính khoảng cách
        if (result == ReviewResult.AGAIN) return now

        // Số ngày cơ bản tùy mức đánh giá
        val baseIntervalDays = when (result) {
            ReviewResult.HARD -> 1
            ReviewResult.GOOD -> 3
            ReviewResult.EASY -> 7
            ReviewResult.AGAIN -> 0 // đã xử lý ở trên
        }

        // Nhân với easeFactor: từ dễ nhớ → khoảng cách ngày càng dài
        val intervalMillis = (baseIntervalDays * easeFactor * TimeUnit.DAYS.toMillis(1)).toLong()
        return now + intervalMillis
    }

    fun adjustEaseFactor(currentEaseFactor: Float, result: ReviewResult): Float {
        val adjusted = when (result) {
            ReviewResult.AGAIN -> currentEaseFactor - 0.2f  // khó → ôn thường xuyên hơn
            ReviewResult.HARD  -> currentEaseFactor - 0.1f
            ReviewResult.GOOD  -> currentEaseFactor          // giữ nguyên
            ReviewResult.EASY  -> currentEaseFactor + 0.15f // dễ → ôn ít lại
        }
        // Không để easeFactor xuống dưới 1.3 để tránh khoảng cách ôn quá ngắn
        return adjusted.coerceAtLeast(MIN_EASE_FACTOR)
    }

    fun applyReview(
        currentEaseFactor: Float,
        result: ReviewResult,
        now: Long
    ): SrsReviewOutcome {
        val newEaseFactor = adjustEaseFactor(currentEaseFactor, result)
        return SrsReviewOutcome(
            nextReviewAt = calculateNextReview(result, now, newEaseFactor),
            easeFactor = newEaseFactor,
            isCorrect = result != ReviewResult.AGAIN
        )
    }
}
