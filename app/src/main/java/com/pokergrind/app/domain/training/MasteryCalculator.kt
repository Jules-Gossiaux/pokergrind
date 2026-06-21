package com.pokergrind.app.domain.training

import com.pokergrind.app.domain.model.PokerAction
import kotlin.math.roundToInt

data class AnswerSnapshot(
    val handNotation: String,
    val expectedAction: PokerAction,
    val isCorrect: Boolean,
)

data class SpotMastery(
    val answerCount: Int,
    val correctCount: Int,
    val successRatePercent: Int,
    val distinctOpenHands: Int,
    val distinctFoldHands: Int,
    val isMastered: Boolean,
) {
    val answersRemaining: Int
        get() = (MasteryCalculator.WINDOW_SIZE - answerCount).coerceAtLeast(0)
}

object MasteryCalculator {
    const val WINDOW_SIZE = 30
    const val REQUIRED_CORRECT = 27
    const val REQUIRED_DISTINCT_PER_ACTION = 8

    fun calculate(recentAnswers: List<AnswerSnapshot>): SpotMastery {
        val window = recentAnswers.take(WINDOW_SIZE)
        val correctCount = window.count(AnswerSnapshot::isCorrect)
        val distinctOpenHands = window
            .filter { it.expectedAction == PokerAction.OPEN }
            .map(AnswerSnapshot::handNotation)
            .distinct()
            .size
        val distinctFoldHands = window
            .filter { it.expectedAction == PokerAction.FOLD }
            .map(AnswerSnapshot::handNotation)
            .distinct()
            .size
        val successRate = if (window.isEmpty()) {
            0
        } else {
            (correctCount.toDouble() / window.size * 100).roundToInt()
        }

        return SpotMastery(
            answerCount = window.size,
            correctCount = correctCount,
            successRatePercent = successRate,
            distinctOpenHands = distinctOpenHands,
            distinctFoldHands = distinctFoldHands,
            isMastered = window.size == WINDOW_SIZE &&
                correctCount >= REQUIRED_CORRECT &&
                distinctOpenHands >= REQUIRED_DISTINCT_PER_ACTION &&
                distinctFoldHands >= REQUIRED_DISTINCT_PER_ACTION,
        )
    }

    val empty: SpotMastery
        get() = calculate(emptyList())
}
