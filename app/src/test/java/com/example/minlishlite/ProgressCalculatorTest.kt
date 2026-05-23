package com.example.minlishlite

import com.example.minlishlite.domain.model.ReviewHistory
import com.example.minlishlite.domain.model.ReviewResult
import com.example.minlishlite.domain.usecase.ProgressCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class ProgressCalculatorTest {

    private val zoneId = ZoneId.of("UTC")
    private val today = LocalDate.of(2024, 6, 15)
    private val todayStart = today.atStartOfDay(zoneId).toInstant().toEpochMilli()

    @Test
    fun accuracy_zeroReviews_returnsZero() {
        assertEquals(0, ProgressCalculator.computeAccuracyPercent(0, 0))
    }

    @Test
    fun accuracy_8of10_returns80() {
        assertEquals(80, ProgressCalculator.computeAccuracyPercent(8, 10))
    }

    @Test
    fun retention_goodAndEasyOverTotal() {
        val history = listOf(
            historyEntry(ReviewResult.GOOD, 0),
            historyEntry(ReviewResult.EASY, 1),
            historyEntry(ReviewResult.AGAIN, 2),
            historyEntry(ReviewResult.HARD, 3)
        )
        assertEquals(50, ProgressCalculator.computeRetentionPercent(history))
    }

    @Test
    fun retention_empty_returnsZero() {
        assertEquals(0, ProgressCalculator.computeRetentionPercent(emptyList()))
    }

    @Test
    fun streak_consecutiveDays() {
        val history = listOf(
            historyOnDay(today, 0),
            historyOnDay(today.minusDays(1), 1),
            historyOnDay(today.minusDays(2), 2)
        )
        assertEquals(3, ProgressCalculator.computeStreak(history, today, zoneId))
    }

    @Test
    fun streak_breaksOnGap() {
        val history = listOf(
            historyOnDay(today, 0),
            historyOnDay(today.minusDays(2), 1)
        )
        assertEquals(1, ProgressCalculator.computeStreak(history, today, zoneId))
    }

    @Test
    fun streak_todayEmpty_yesterdayCounted() {
        val history = listOf(
            historyOnDay(today.minusDays(1), 0),
            historyOnDay(today.minusDays(2), 1)
        )
        assertEquals(2, ProgressCalculator.computeStreak(history, today, zoneId))
    }

    @Test
    fun streak_noActivity_returnsZero() {
        assertEquals(0, ProgressCalculator.computeStreak(emptyList(), today, zoneId))
    }

    @Test
    fun level_beginner_intermediate_advanced() {
        assertEquals(ProgressCalculator.LEVEL_BEGINNER, ProgressCalculator.estimateLevel(50))
        assertEquals(ProgressCalculator.LEVEL_INTERMEDIATE, ProgressCalculator.estimateLevel(300))
        assertEquals(ProgressCalculator.LEVEL_INTERMEDIATE, ProgressCalculator.estimateLevel(1000))
        assertEquals(ProgressCalculator.LEVEL_ADVANCED, ProgressCalculator.estimateLevel(1001))
    }

    @Test
    fun weeklyActivity_groupsByDay() {
        val history = listOf(
            historyOnDay(today, 0),
            historyOnDay(today, 1),
            historyOnDay(today.minusDays(3), 2)
        )
        val activity = ProgressCalculator.buildWeeklyActivity(history, today, zoneId)
        assertEquals(7, activity.size)
        assertEquals(2, activity.last().reviewCount)
        assertEquals(1, activity[activity.size - 4].reviewCount)
    }

    @Test
    fun compute_buildsFullAnalytics() {
        val analytics = ProgressCalculator.compute(
            totalWords = 10,
            wordsLearned = 5,
            dueToday = 2,
            totalCorrect = 4,
            totalReviews = 5,
            reviewHistory = listOf(historyEntry(ReviewResult.GOOD, 0)),
            now = todayStart,
            zoneId = zoneId
        )
        assertEquals(10, analytics.totalWords)
        assertEquals(80, analytics.accuracyPercent)
        assertEquals(100, analytics.retentionPercent)
        assertTrue(analytics.achievements.first().unlocked)
    }

    private fun historyEntry(result: ReviewResult, offsetMillis: Long): ReviewHistory {
        return ReviewHistory(
            wordId = 1,
            deckId = 1,
            result = result.name,
            reviewedAt = todayStart + offsetMillis
        )
    }

    private fun historyOnDay(date: LocalDate, index: Int): ReviewHistory {
        val millis = date.atStartOfDay(zoneId).toInstant().toEpochMilli() + index
        return ReviewHistory(
            wordId = 1,
            deckId = 1,
            result = ReviewResult.GOOD.name,
            reviewedAt = millis
        )
    }
}
