package com.example.minlishlite.core.util

import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import com.example.minlishlite.data.model.ReviewResult
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class ProgressCalculatorTest {

    @Test
    fun `computeAccuracyPercent returns correct value`() {
        assertEquals(0, ProgressCalculator.computeAccuracyPercent(0, 0))
        assertEquals(50, ProgressCalculator.computeAccuracyPercent(5, 10))
        assertEquals(100, ProgressCalculator.computeAccuracyPercent(10, 10))
        assertEquals(33, ProgressCalculator.computeAccuracyPercent(1, 3))
    }

    @Test
    fun `computeRetentionPercent calculates based on GOOD and EASY`() {
        val history = listOf(
            ReviewHistoryEntity(1, 1, 1, ReviewResult.GOOD.name, 1000),
            ReviewHistoryEntity(2, 1, 1, ReviewResult.EASY.name, 1000),
            ReviewHistoryEntity(3, 1, 1, ReviewResult.HARD.name, 1000),
            ReviewHistoryEntity(4, 1, 1, ReviewResult.AGAIN.name, 1000)
        )
        // 2 positive out of 4 = 50%
        assertEquals(50, ProgressCalculator.computeRetentionPercent(history))
    }

    @Test
    fun `estimateLevel returns correct levels based on words learned`() {
        assertEquals(ProgressCalculator.LEVEL_BEGINNER, ProgressCalculator.estimateLevel(0))
        assertEquals(ProgressCalculator.LEVEL_BEGINNER, ProgressCalculator.estimateLevel(299))
        assertEquals(ProgressCalculator.LEVEL_INTERMEDIATE, ProgressCalculator.estimateLevel(300))
        assertEquals(ProgressCalculator.LEVEL_INTERMEDIATE, ProgressCalculator.estimateLevel(1000))
        assertEquals(ProgressCalculator.LEVEL_ADVANCED, ProgressCalculator.estimateLevel(1001))
    }

    @Test
    fun `computeStreak counts consecutive days including today or yesterday`() {
        val zone = ZoneId.of("UTC")
        val today = LocalDate.of(2026, 6, 5)
        
        // 2026-06-05, 04, 03 -> Streak 3
        val history = listOf(
            createHistoryForDate(today, zone),
            createHistoryForDate(today.minusDays(1), zone),
            createHistoryForDate(today.minusDays(2), zone),
            createHistoryForDate(today.minusDays(4), zone) // Gap!
        )
        
        val streak = ProgressCalculator.computeStreak(history, today, zone)
        assertEquals(3, streak)
    }

    @Test
    fun `computeStreak continues if yesterday was active but today is not`() {
        val zone = ZoneId.of("UTC")
        val today = LocalDate.of(2026, 6, 5)
        
        // 2026-06-04, 03 -> Streak 2
        val history = listOf(
            createHistoryForDate(today.minusDays(1), zone),
            createHistoryForDate(today.minusDays(2), zone)
        )
        
        val streak = ProgressCalculator.computeStreak(history, today, zone)
        assertEquals(2, streak)
    }
    
    @Test
    fun `computeStreak breaks if neither today nor yesterday active`() {
        val zone = ZoneId.of("UTC")
        val today = LocalDate.of(2026, 6, 5)
        
        // 2026-06-03 -> Streak 0
        val history = listOf(
            createHistoryForDate(today.minusDays(2), zone)
        )
        
        val streak = ProgressCalculator.computeStreak(history, today, zone)
        assertEquals(0, streak)
    }

    private fun createHistoryForDate(date: LocalDate, zone: ZoneId): ReviewHistoryEntity {
        val millis = date.atStartOfDay(zone).toInstant().toEpochMilli()
        return ReviewHistoryEntity(
            id = 0, wordId = 1, deckId = 1, result = ReviewResult.GOOD.name, reviewedAt = millis
        )
    }
}
