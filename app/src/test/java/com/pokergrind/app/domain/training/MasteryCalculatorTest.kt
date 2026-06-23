package com.pokergrind.app.domain.training

import com.pokergrind.app.domain.model.PokerAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MasteryCalculatorTest {
    @Test
    fun `spot is mastered with 27 correct answers and enough diversity`() {
        val answers = buildAnswers(correctCount = 27)

        val mastery = MasteryCalculator.calculate(answers)

        assertTrue(mastery.isMastered)
        assertEquals(27, mastery.correctCount)
        assertEquals(90, mastery.successRatePercent)
    }

    @Test
    fun `spot is not mastered before thirty answers`() {
        val mastery = MasteryCalculator.calculate(buildAnswers(correctCount = 27).take(29))

        assertFalse(mastery.isMastered)
        assertEquals(1, mastery.answersRemaining)
    }

    @Test
    fun `spot is not mastered without enough hand diversity`() {
        val answers = List(30) { index ->
            AnswerSnapshot(
                handNotation = if (index % 2 == 0) "AA" else "72o",
                expectedAction = if (index % 2 == 0) PokerAction.OPEN else PokerAction.FOLD,
                isCorrect = index < 27,
            )
        }

        val mastery = MasteryCalculator.calculate(answers)

        assertFalse(mastery.isMastered)
        assertEquals(1, mastery.distinctOpenHands)
        assertEquals(1, mastery.distinctFoldHands)
    }

    @Test
    fun `only the thirty most recent answers count`() {
        val oldErrors = List(10) {
            AnswerSnapshot("72o", PokerAction.FOLD, false)
        }
        val mastery = MasteryCalculator.calculate(buildAnswers(correctCount = 30) + oldErrors)

        assertTrue(mastery.isMastered)
        assertEquals(30, mastery.answerCount)
        assertEquals(30, mastery.correctCount)
    }

    @Test
    fun `three action spot requires coverage for call three bet and fold`() {
        val answers = List(30) { index ->
            val action = when (index % 3) {
                0 -> PokerAction.CALL
                1 -> PokerAction.THREE_BET
                else -> PokerAction.FOLD
            }
            AnswerSnapshot(
                handNotation = "${action.name}-$index",
                expectedAction = action,
                isCorrect = index < 27,
            )
        }

        val mastery = MasteryCalculator.calculate(
            recentAnswers = answers,
            requiredActions = setOf(PokerAction.CALL, PokerAction.THREE_BET, PokerAction.FOLD),
        )

        assertTrue(mastery.isMastered)
        assertEquals(5, MasteryCalculator.requiredDistinctFor(actionCount = 3))
    }

    private fun buildAnswers(correctCount: Int): List<AnswerSnapshot> {
        val openHands = listOf("AA", "KK", "QQ", "JJ", "TT", "99", "AKs", "AQs", "AJs", "KQs", "A8s", "A2o", "87o", "54s", "76s")
        val foldHands = listOf("72o", "82o", "92o", "T2o", "J2o", "Q2o", "K2o", "32o", "42o", "52o", "62o", "73o", "83o", "93o", "T3o")

        return List(30) { index ->
            val isOpen = index % 2 == 0
            val handIndex = index / 2
            AnswerSnapshot(
                handNotation = if (isOpen) openHands[handIndex] else foldHands[handIndex],
                expectedAction = if (isOpen) PokerAction.OPEN else PokerAction.FOLD,
                isCorrect = index < correctCount,
            )
        }
    }
}
