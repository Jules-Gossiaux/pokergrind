package com.pokergrind.app.data

import com.pokergrind.app.domain.model.PokerAction
import org.junit.Assert.assertEquals
import org.junit.Test

class BtnOpenRangeTest {
    private val range = BtnOpenRange.definition

    @Test
    fun `source range has the documented combo count and percentage`() {
        assertEquals(602, range.openComboCount)
        assertEquals(45.40, range.openPercentage, 0.001)
    }

    @Test
    fun `boundary hands match the source image`() {
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(PokerAction.OPEN, actions.getValue("A2o"))
        assertEquals(PokerAction.OPEN, actions.getValue("87o"))
        assertEquals(PokerAction.OPEN, actions.getValue("54s"))
        assertEquals(PokerAction.FOLD, actions.getValue("K7o"))
        assertEquals(PokerAction.FOLD, actions.getValue("Q6s"))
        assertEquals(PokerAction.FOLD, actions.getValue("43s"))
    }
}
