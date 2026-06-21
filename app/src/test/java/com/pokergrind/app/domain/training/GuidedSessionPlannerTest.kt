package com.pokergrind.app.domain.training

import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.data.CoOpenRange
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

    @Test
    fun `every unlocked range is represented in a guided session`() {
        val btn = BtnOpenRange.definition
        val co = CoOpenRange.definition

        val session = GuidedSessionPlanner.plan(
            ranges = listOf(btn, co),
            reviewStates = emptyMap(),
            nowEpochMillis = 10_000,
            random = Random(7),
        )
        val counts = session.groupingBy { it.first }.eachCount()

        assertEquals(20, session.size)
        assertTrue(counts.getValue(btn.id) >= 4)
        assertTrue(counts.getValue(co.id) >= 4)
    }
}
