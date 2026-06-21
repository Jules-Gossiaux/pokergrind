package com.pokergrind.app.data

import com.pokergrind.app.domain.model.PokerAction
import org.junit.Assert.assertEquals
import org.junit.Test

class AdditionalOpenRangesTest {
    @Test
    fun `HJ source range has documented combo count and boundaries`() {
        val range = HjOpenRange.definition
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(190, range.openComboCount)
        assertEquals(14.33, range.openPercentage, 0.01)
        assertEquals(PokerAction.OPEN, actions.getValue("KJo"))
        assertEquals(PokerAction.OPEN, actions.getValue("QJo"))
        assertEquals(PokerAction.FOLD, actions.getValue("ATo"))
    }

    @Test
    fun `UTG source range has documented combo count and boundaries`() {
        val range = UtgOpenRange.definition
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(166, range.openComboCount)
        assertEquals(12.52, range.openPercentage, 0.01)
        assertEquals(PokerAction.OPEN, actions.getValue("AJo"))
        assertEquals(PokerAction.OPEN, actions.getValue("JTs"))
        assertEquals(PokerAction.FOLD, actions.getValue("KJo"))
    }

    @Test
    fun `SB source range has documented combo count and boundaries`() {
        val range = SbOpenRange.definition
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(418, range.openComboCount)
        assertEquals(31.52, range.openPercentage, 0.01)
        assertEquals(PokerAction.OPEN, actions.getValue("K8s"))
        assertEquals(PokerAction.OPEN, actions.getValue("75s"))
        assertEquals(PokerAction.FOLD, actions.getValue("A7o"))
        assertEquals(PokerAction.FOLD, actions.getValue("54s"))
    }
}
