package com.pokergrind.app.data

import com.pokergrind.app.domain.model.PokerAction
import org.junit.Assert.assertEquals
import org.junit.Test

class BbDefenseRangesTest {
    @Test
    fun `BB vs BTN range has documented combo counts and boundaries`() {
        val range = BbVsBtnDefenseRange.definition
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
}
