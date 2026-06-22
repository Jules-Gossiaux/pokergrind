package com.pokergrind.app.domain.training

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpacedRepetitionTest {
    private val now = 1_000_000L
    private val day = 24L * 60 * 60 * 1_000

    @Test
    fun `guided correct answers expand intervals`() {
        val first = SpacedRepetition.afterGuidedAnswer(null, true, now)
        val second = SpacedRepetition.afterGuidedAnswer(first, true, now)
        val third = SpacedRepetition.afterGuidedAnswer(second, true, now)
        val fourth = SpacedRepetition.afterGuidedAnswer(third, true, now)

        assertEquals(1, first.intervalDays)
        assertEquals(3, second.intervalDays)
        assertEquals(7, third.intervalDays)
        assertEquals(14, fourth.intervalDays)
        assertEquals(now + day, first.dueAtEpochMillis)
    }

    @Test
    fun `guided error makes item immediately due and high priority`() {
        val state = SpacedRepetition.afterGuidedAnswer(
            current = ReviewState(stage = 3, intervalDays = 7),
            isCorrect = false,
            nowEpochMillis = now,
        )

        assertEquals(now, state.dueAtEpochMillis)
        assertEquals(0, state.stage)
        assertEquals(1, state.lapses)
        assertTrue(state.priorityBoost >= 4)
    }

    @Test
    fun `repeated guided errors progressively increase priority`() {
        val first = SpacedRepetition.afterGuidedAnswer(null, false, now)
        val second = SpacedRepetition.afterGuidedAnswer(first, false, now + 1)
        val third = SpacedRepetition.afterGuidedAnswer(second, false, now + 2)

        assertEquals(4, first.priorityBoost)
        assertEquals(5, second.priorityBoost)
        assertEquals(6, third.priorityBoost)
        assertEquals(3, third.lapses)
    }

    @Test
    fun `free error boosts guided priority`() {
        val state = SpacedRepetition.afterFreeError(null, now)

        assertEquals(now, state.dueAtEpochMillis)
        assertTrue(state.priorityBoost >= 5)
    }
}
