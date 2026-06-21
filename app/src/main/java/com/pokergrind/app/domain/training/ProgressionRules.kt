package com.pokergrind.app.domain.training

import java.time.LocalDate

object ProgressionRules {
    fun nextStreak(
        currentStreak: Int,
        lastCompletedDate: LocalDate?,
        completedDate: LocalDate,
    ): Int = when (lastCompletedDate) {
        completedDate -> currentStreak
        completedDate.minusDays(1) -> currentStreak + 1
        else -> 1
    }

    fun visibleStreak(
        storedStreak: Int,
        lastCompletedDate: LocalDate?,
        today: LocalDate,
    ): Int = when (lastCompletedDate) {
        today, today.minusDays(1) -> storedStreak
        else -> 0
    }
}
