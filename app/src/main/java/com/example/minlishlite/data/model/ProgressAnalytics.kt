package com.example.minlishlite.data.model

data class ProgressAnalytics(
    val totalWords: Int,
    val wordsLearned: Int,
    val dueToday: Int,
    val accuracyPercent: Int,
    val streakDays: Int,
    val retentionPercent: Int,
    val levelLabel: String,
    val weeklyActivity: List<DayActivity>,
    val achievements: List<Achievement>
)

data class DayActivity(
    val label: String,
    val reviewCount: Int
)

data class Achievement(
    val title: String,
    val description: String,
    val unlocked: Boolean
)
