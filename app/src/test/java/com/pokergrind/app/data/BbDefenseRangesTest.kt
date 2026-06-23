package com.pokergrind.app.data

import com.pokergrind.app.domain.model.PokerAction
import org.junit.Assert.assertEquals
import org.junit.Test

class BbDefenseRangesTest {
    @Test
    fun `BB vs BTN range has documented combo counts and boundaries`() {
        val range = BbDefenseRanges.vsBtn
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(70, range.comboCountFor(PokerAction.THREE_BET))
        assertEquals(204, range.comboCountFor(PokerAction.CALL))
        assertEquals(5.28, range.percentageFor(PokerAction.THREE_BET), 0.01)
        assertEquals(15.38, range.percentageFor(PokerAction.CALL), 0.01)
        assertEquals(20.66, range.percentageFor(PokerAction.THREE_BET) + range.percentageFor(PokerAction.CALL), 0.01)

        assertEquals(PokerAction.THREE_BET, actions.getValue("TT"))
        assertEquals(PokerAction.THREE_BET, actions.getValue("AQo"))
        assertEquals(PokerAction.CALL, actions.getValue("99"))
        assertEquals(PokerAction.CALL, actions.getValue("A2s"))
        assertEquals(PokerAction.CALL, actions.getValue("KJo"))
        assertEquals(PokerAction.FOLD, actions.getValue("A9o"))
        assertEquals(PokerAction.FOLD, actions.getValue("K8s"))
        assertEquals(PokerAction.FOLD, actions.getValue("54s"))
    }

    @Test
    fun `BB vs CO range has documented combo counts and boundaries`() {
        val range = BbDefenseRanges.vsCo
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(58, range.comboCountFor(PokerAction.THREE_BET))
        assertEquals(160, range.comboCountFor(PokerAction.CALL))
        assertEquals(PokerAction.THREE_BET, actions.getValue("TT"))
        assertEquals(PokerAction.THREE_BET, actions.getValue("AJs"))
        assertEquals(PokerAction.CALL, actions.getValue("AJo"))
        assertEquals(PokerAction.CALL, actions.getValue("76s"))
        assertEquals(PokerAction.FOLD, actions.getValue("K9s"))
    }

    @Test
    fun `BB vs SB range has documented combo counts and boundaries`() {
        val range = BbDefenseRanges.vsSb
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(108, range.comboCountFor(PokerAction.THREE_BET))
        assertEquals(354, range.comboCountFor(PokerAction.CALL))
        assertEquals(PokerAction.THREE_BET, actions.getValue("99"))
        assertEquals(PokerAction.THREE_BET, actions.getValue("A2s"))
        assertEquals(PokerAction.CALL, actions.getValue("ATs"))
        assertEquals(PokerAction.CALL, actions.getValue("K2s"))
        assertEquals(PokerAction.CALL, actions.getValue("54s"))
        assertEquals(PokerAction.FOLD, actions.getValue("Q4s"))
    }

    @Test
    fun `BB vs HJ range has documented combo counts and boundaries`() {
        val range = BbDefenseRanges.vsHj
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(38, range.comboCountFor(PokerAction.THREE_BET))
        assertEquals(108, range.comboCountFor(PokerAction.CALL))
        assertEquals(PokerAction.THREE_BET, actions.getValue("AQs"))
        assertEquals(PokerAction.CALL, actions.getValue("AJs"))
        assertEquals(PokerAction.CALL, actions.getValue("87s"))
        assertEquals(PokerAction.FOLD, actions.getValue("AJo"))
    }

    @Test
    fun `BB vs UTG range has documented combo counts and boundaries`() {
        val range = BbDefenseRanges.vsUtg
        val actions = range.entries.associate { it.hand.notation to it.action }

        assertEquals(34, range.comboCountFor(PokerAction.THREE_BET))
        assertEquals(100, range.comboCountFor(PokerAction.CALL))
        assertEquals(PokerAction.THREE_BET, actions.getValue("AKo"))
        assertEquals(PokerAction.CALL, actions.getValue("JJ"))
        assertEquals(PokerAction.CALL, actions.getValue("AQo"))
        assertEquals(PokerAction.FOLD, actions.getValue("ATs"))
    }

    @Test
    fun `BB defense chapter exposes all five spots in pedagogical order`() {
        assertEquals(
            listOf("BB vs BTN", "BB vs CO", "BB vs SB", "BB vs HJ", "BB vs UTG"),
            BbDefenseRanges.all.map { it.title },
        )
    }
}
