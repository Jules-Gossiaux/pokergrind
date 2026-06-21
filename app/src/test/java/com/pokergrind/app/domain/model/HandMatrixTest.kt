package com.pokergrind.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HandMatrixTest {
    @Test
    fun `matrix contains the 169 unique canonical categories`() {
        assertEquals(169, HandMatrix.allHands.size)
        assertEquals(169, HandMatrix.allHands.map { it.notation }.distinct().size)
    }

    @Test
    fun `matrix contains expected corners and diagonal`() {
        assertEquals("AA", HandMatrix.allHands.first().notation)
        assertEquals("A2s", HandMatrix.allHands[12].notation)
        assertEquals("A2o", HandMatrix.allHands[156].notation)
        assertEquals("22", HandMatrix.allHands.last().notation)
        assertTrue(HandMatrix.allHands.filter { it.shape == HandShape.PAIR }.all { it.comboCount == 6 })
    }
}
