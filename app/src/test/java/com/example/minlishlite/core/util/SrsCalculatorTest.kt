package com.example.minlishlite.core.util

import com.example.minlishlite.data.model.ReviewResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class SrsCalculatorTest {

    // ─── calculateNextReview ──────────────────────────────────────────────────

    @Test
    fun `AGAIN luon tra ve thoi diem hien tai, khong tinh easeFactor`() {
        val now = 1000L
        val result = SrsCalculator.calculateNextReview(ReviewResult.AGAIN, now, easeFactor = 2.5f)
        assertEquals(now, result)
    }

    @Test
    fun `HARD voi easeFactor mac dinh 2_5 cho khoang cach 2_5 ngay`() {
        val now = 0L
        // HARD base = 1 ngày × 2.5 = 2.5 ngày
        val expected = (1 * 2.5f * TimeUnit.DAYS.toMillis(1)).toLong()
        val result = SrsCalculator.calculateNextReview(ReviewResult.HARD, now, easeFactor = 2.5f)
        assertEquals(expected, result)
    }

    @Test
    fun `GOOD voi easeFactor mac dinh 2_5 cho khoang cach 7_5 ngay`() {
        val now = 0L
        // GOOD base = 3 ngày × 2.5 = 7.5 ngày
        val expected = (3 * 2.5f * TimeUnit.DAYS.toMillis(1)).toLong()
        val result = SrsCalculator.calculateNextReview(ReviewResult.GOOD, now, easeFactor = 2.5f)
        assertEquals(expected, result)
    }

    @Test
    fun `EASY voi easeFactor mac dinh 2_5 cho khoang cach 17_5 ngay`() {
        val now = 0L
        // EASY base = 7 ngày × 2.5 = 17.5 ngày
        val expected = (7 * 2.5f * TimeUnit.DAYS.toMillis(1)).toLong()
        val result = SrsCalculator.calculateNextReview(ReviewResult.EASY, now, easeFactor = 2.5f)
        assertEquals(expected, result)
    }

    @Test
    fun `easeFactor thap hon cho khoang cach ngan hon - GOOD voi easeFactor 1_3`() {
        val now = 0L
        // easeFactor nhỏ (từ khó nhớ) → khoảng cách ngắn hơn
        val expected = (3 * 1.3f * TimeUnit.DAYS.toMillis(1)).toLong()
        val result = SrsCalculator.calculateNextReview(ReviewResult.GOOD, now, easeFactor = 1.3f)
        assertEquals(expected, result)
    }

    @Test
    fun `easeFactor cao hon cho khoang cach dai hon - GOOD voi easeFactor 3_0`() {
        val now = 0L
        // easeFactor lớn (từ dễ nhớ) → khoảng cách dài hơn
        val expected = (3 * 3.0f * TimeUnit.DAYS.toMillis(1)).toLong()
        val result = SrsCalculator.calculateNextReview(ReviewResult.GOOD, now, easeFactor = 3.0f)
        assertEquals(expected, result)
    }

    // ─── adjustEaseFactor ─────────────────────────────────────────────────────

    @Test
    fun `AGAIN giam easeFactor 0_2 va giu nguong toi thieu 1_3`() {
        assertEquals(2.3f, SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.AGAIN), 0.01f)
        // Không được xuống dưới 1.3
        assertEquals(1.3f, SrsCalculator.adjustEaseFactor(1.4f, ReviewResult.AGAIN), 0.01f)
    }

    @Test
    fun `HARD giam easeFactor 0_1`() {
        assertEquals(2.4f, SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.HARD), 0.01f)
    }

    @Test
    fun `GOOD giu nguyen easeFactor`() {
        assertEquals(2.5f, SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.GOOD), 0.01f)
    }

    @Test
    fun `EASY tang easeFactor 0_15`() {
        assertEquals(2.65f, SrsCalculator.adjustEaseFactor(2.5f, ReviewResult.EASY), 0.01f)
    }

    // ─── applyReview (hàm tổng hợp) ──────────────────────────────────────────

    @Test
    fun `applyReview GOOD dung easeFactor moi de tinh nextReviewAt`() {
        val now = 0L
        // GOOD giữ nguyên easeFactor = 2.5 → nextReviewAt = 3 × 2.5 ngày
        val outcome = SrsCalculator.applyReview(currentEaseFactor = 2.5f, ReviewResult.GOOD, now)

        val expectedInterval = (3 * 2.5f * TimeUnit.DAYS.toMillis(1)).toLong()
        assertEquals(expectedInterval, outcome.nextReviewAt)
        assertEquals(2.5f, outcome.easeFactor, 0.01f)
        assertTrue(outcome.isCorrect)
    }

    @Test
    fun `applyReview EASY tang easeFactor va keo dai khoang cach on`() {
        val now = 0L
        // EASY: easeFactor tăng từ 2.5 lên 2.65 → khoảng cách = 7 × 2.65 ngày
        val outcome = SrsCalculator.applyReview(currentEaseFactor = 2.5f, ReviewResult.EASY, now)

        val expectedEase = 2.65f
        val expectedInterval = (7 * expectedEase * TimeUnit.DAYS.toMillis(1)).toLong()
        assertEquals(expectedInterval, outcome.nextReviewAt)
        assertEquals(expectedEase, outcome.easeFactor, 0.01f)
        assertTrue(outcome.isCorrect)
    }

    @Test
    fun `applyReview AGAIN tra ve ngay va danh dau isCorrect false`() {
        val now = 1000L
        val outcome = SrsCalculator.applyReview(currentEaseFactor = 2.5f, ReviewResult.AGAIN, now)

        assertEquals(now, outcome.nextReviewAt)
        assertFalse(outcome.isCorrect)
    }
}
