package com.pokergrind.app.domain.training

import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.domain.model.PokerAction
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionFactoryTest {
    @Test
    fun `daily session contains ten open and ten fold hands without duplicates`() {
        val range = BtnOpenRange.definition
        val session = SessionFactory.createBalancedSession(range, Random(42))
        val actionCounts = session.groupingBy(range::actionFor).eachCount()

        assertEquals(20, session.size)
        assertEquals(20, session.distinctBy { it.notation }.size)
        assertEquals(10, actionCounts.getValue(PokerAction.OPEN))
        assertEquals(10, actionCounts.getValue(PokerAction.FOLD))
    }
}
