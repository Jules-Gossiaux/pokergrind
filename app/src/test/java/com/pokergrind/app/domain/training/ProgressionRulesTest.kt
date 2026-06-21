package com.pokergrind.app.domain.training

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressionRulesTest {
    private val today = LocalDate.of(2026, 6, 21)

    @Test
    fun `completing consecutive days increments streak`() {
        assertEquals(
            4,
            ProgressionRules.nextStreak(
                currentStreak = 3,
                lastCompletedDate = today.minusDays(1),
                completedDate = today,
            ),
        )
    }

    @Test
    fun `second completion on same day does not increment streak`() {
        assertEquals(
            3,
            ProgressionRules.nextStreak(
                currentStreak = 3,
                lastCompletedDate = today,
                completedDate = today,
            ),
        )
    }

    @Test
    fun `missing a day resets visible streak`() {
        assertEquals(
            0,
            ProgressionRules.visibleStreak(
                storedStreak = 8,
                lastCompletedDate = today.minusDays(2),
                today = today,
            ),
        )
    }
}
