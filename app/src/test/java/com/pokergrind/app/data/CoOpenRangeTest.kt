package com.pokergrind.app.data

import com.pokergrind.app.domain.model.PokerAction
import org.junit.Assert.assertEquals
import org.junit.Test

class CoOpenRangeTest {
    private val range = CoOpenRange.definition

    @Test
    fun `source range has documented combo count and percentage`() {
        assertEquals(302, range.openComboCount)
        assertEquals(22.78, range.openPercentage, 0.01)
    }

    @Test
    fun `boundary hands match source image`() {
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(PokerAction.OPEN, actions.getValue("K9s"))
        assertEquals(PokerAction.OPEN, actions.getValue("Q9s"))
        assertEquals(PokerAction.OPEN, actions.getValue("J9s"))
        assertEquals(PokerAction.OPEN, actions.getValue("65s"))
        assertEquals(PokerAction.FOLD, actions.getValue("K8s"))
        assertEquals(PokerAction.FOLD, actions.getValue("T8s"))
        assertEquals(PokerAction.FOLD, actions.getValue("54s"))
    }
}
