package com.example.minlishlite.core.notification

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object ReminderTimeCalculator {

    fun delayUntilNextReminder(
        reminderTime: String,
        now: LocalDateTime = LocalDateTime.now(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Duration {
        val (hour, minute) = parseTime(reminderTime)
        val targetTime = LocalTime.of(hour, minute)
        var targetDateTime = LocalDateTime.of(now.toLocalDate(), targetTime)

        if (!targetDateTime.isAfter(now)) {
            targetDateTime = LocalDateTime.of(now.toLocalDate().plusDays(1), targetTime)
        }

        return Duration.between(now, targetDateTime)
    }

    fun parseTime(reminderTime: String): Pair<Int, Int> {
        val parts = reminderTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 9
        val minute = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0
        return hour to minute
    }
}
