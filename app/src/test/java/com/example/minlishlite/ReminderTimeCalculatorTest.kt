package com.example.minlishlite

import com.example.minlishlite.core.notification.ReminderTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class ReminderTimeCalculatorTest {

    private val zoneId = ZoneId.of("UTC")

    @Test
    fun delayUntilNextReminder_beforeTimeToday_returnsRemainingToday() {
        val now = LocalDateTime.of(2024, 6, 15, 8, 0)
        val delay = ReminderTimeCalculator.delayUntilNextReminder("09:00", now, zoneId)
        assertEquals(60L, delay.toMinutes())
    }

    @Test
    fun delayUntilNextReminder_afterTimeToday_returnsTomorrow() {
        val now = LocalDateTime.of(2024, 6, 15, 10, 0)
        val delay = ReminderTimeCalculator.delayUntilNextReminder("09:00", now, zoneId)
        assertEquals(23 * 60L, delay.toMinutes())
    }

    @Test
    fun delayUntilNextReminder_atExactTime_returnsTomorrow() {
        val now = LocalDateTime.of(2024, 6, 15, 9, 0)
        val delay = ReminderTimeCalculator.delayUntilNextReminder("09:00", now, zoneId)
        assertTrue(delay.toHours() >= 23)
    }

    @Test
    fun parseTime_invalidParts_usesDefaults() {
        val (hour, minute) = ReminderTimeCalculator.parseTime("invalid")
        assertEquals(9, hour)
        assertEquals(0, minute)
    }

    @Test
    fun parseTime_validFormat() {
        val (hour, minute) = ReminderTimeCalculator.parseTime("14:30")
        assertEquals(14, hour)
        assertEquals(30, minute)
    }
}
