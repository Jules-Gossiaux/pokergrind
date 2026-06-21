package com.pokergrind.app.domain.training

import com.pokergrind.app.data.BtnOpenRange
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GuidedSessionPlannerTest {
    @Test
    fun `overdue boosted error is selected in next guided session`() {
        val range = BtnOpenRange.definition
        val weakHand = "72o"
        val reviewStates = mapOf(
            (range.id to weakHand) to ReviewState(
                dueAtEpochMillis = 0,
                priorityBoost = 5,
            ),
        )

        val session = GuidedSessionPlanner.plan(
            ranges = listOf(range),
            reviewStates = reviewStates,
            nowEpochMillis = 10_000,
            random = Random(42),
        )

        assertEquals(20, session.size)
        assertTrue(session.any { (_, hand) -> hand.notation == weakHand })
    }
}
