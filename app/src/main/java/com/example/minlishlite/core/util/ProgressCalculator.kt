package com.example.minlishlite.core.util

import com.example.minlishlite.data.local.entity.ReviewHistoryEntity
import com.example.minlishlite.data.model.Achievement
import com.example.minlishlite.data.model.DayActivity
import com.example.minlishlite.data.model.ProgressAnalytics
import com.example.minlishlite.data.model.ReviewResult
import java.time.ZoneId
import java.time.LocalDate
import java.time.Instant
import kotlin.math.roundToInt

object ProgressCalculator {

    const val LEVEL_BEGINNER = "Sơ cấp"
    const val LEVEL_INTERMEDIATE = "Trung cấp"
    const val LEVEL_ADVANCED = "Nâng cao"

    private val DAY_LABELS_VI = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    fun compute(
        totalWords: Int,
        wordsLearned: Int,
        dueToday: Int,
        totalCorrect: Int,
        totalReviews: Int,
        reviewHistory: List<ReviewHistoryEntity>,
        now: Long,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): ProgressAnalytics {
        val accuracyPercent = computeAccuracyPercent(totalCorrect, totalReviews)
        val retentionPercent = computeRetentionPercent(reviewHistory)
        val today = Instant.ofEpochMilli(now).atZone(zoneId).toLocalDate()
        val streakDays = computeStreak(reviewHistory, today, zoneId)
        val weeklyActivity = buildWeeklyActivity(reviewHistory, today, zoneId)
        val levelLabel = estimateLevel(wordsLearned)

        val analytics = ProgressAnalytics(
            totalWords = totalWords,
            wordsLearned = wordsLearned,
            dueToday = dueToday,
            accuracyPercent = accuracyPercent,
            streakDays = streakDays,
            retentionPercent = retentionPercent,
            levelLabel = levelLabel,
            weeklyActivity = weeklyActivity,
            achievements = emptyList()
        )

        return analytics.copy(
            achievements = buildAchievements(analytics, reviewHistory.size)
        )
    }

    fun computeAccuracyPercent(totalCorrect: Int, totalReviews: Int): Int {
        if (totalReviews <= 0) return 0
        return ((totalCorrect.toFloat() / totalReviews.toFloat()) * 100f).roundToInt()
            .coerceIn(0, 100)
    }

    fun computeRetentionPercent(history: List<ReviewHistoryEntity>): Int {
        if (history.isEmpty()) return 0
        val positive = history.count { entry ->
            parseResult(entry.result) in setOf(ReviewResult.GOOD, ReviewResult.EASY)
        }
        return ((positive.toFloat() / history.size.toFloat()) * 100f).roundToInt()
            .coerceIn(0, 100)
    }

    fun computeStreak(
        history: List<ReviewHistoryEntity>,
        today: LocalDate,
        zoneId: ZoneId
    ): Int {
        if (history.isEmpty()) return 0

        val activeDays = history
            .map { Instant.ofEpochMilli(it.reviewedAt).atZone(zoneId).toLocalDate() }
            .toSet()

        var startDay = today
        if (today !in activeDays) {
            startDay = today.minusDays(1)
            if (startDay !in activeDays) return 0
        }

        var streak = 0
        var cursor = startDay
        while (cursor in activeDays) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    fun estimateLevel(wordsLearned: Int): String {
        return when {
            wordsLearned < 300 -> LEVEL_BEGINNER
            wordsLearned <= 1000 -> LEVEL_INTERMEDIATE
            else -> LEVEL_ADVANCED
        }
    }

    fun buildWeeklyActivity(
        history: List<ReviewHistoryEntity>,
        today: LocalDate,
        zoneId: ZoneId
    ): List<DayActivity> {
        val countsByDay = history.groupingBy { entry ->
            Instant.ofEpochMilli(entry.reviewedAt).atZone(zoneId).toLocalDate()
        }.eachCount()

        return (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            DayActivity(
                label = dayLabel(date),
                reviewCount = countsByDay[date] ?: 0
            )
        }
    }

    fun buildAchievements(
        analytics: ProgressAnalytics,
        totalReviewSessions: Int
    ): List<Achievement> {
        return listOf(
            Achievement(
                title = "Bắt đầu hành trình",
                description = "Hoàn thành lần ôn tập đầu tiên",
                unlocked = totalReviewSessions >= 1
            ),
            Achievement(
                title = "Tuần đều đặn",
                description = "Duy trì streak 7 ngày",
                unlocked = analytics.streakDays >= 7
            ),
            Achievement(
                title = "100 từ đã học",
                description = "Đã học ít nhất 100 từ",
                unlocked = analytics.wordsLearned >= 100
            ),
            Achievement(
                title = "Độ chính xác 80%",
                description = "Đạt 80% độ chính xác tổng thể",
                unlocked = analytics.accuracyPercent >= 80
            ),
            Achievement(
                title = "Ghi nhớ tốt",
                description = "Tỷ lệ retention từ 70% trở lên",
                unlocked = analytics.retentionPercent >= 70
            )
        )
    }

    private fun dayLabel(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek.value
        return DAY_LABELS_VI[dayOfWeek - 1]
    }

    private fun parseResult(raw: String): ReviewResult? {
        return runCatching { ReviewResult.valueOf(raw) }.getOrNull()
    }
}
