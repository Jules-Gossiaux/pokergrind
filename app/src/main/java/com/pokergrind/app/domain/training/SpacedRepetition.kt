package com.pokergrind.app.domain.training

import kotlin.math.max

data class ReviewState(
    val dueAtEpochMillis: Long = 0,
    val intervalDays: Int = 0,
    val stage: Int = 0,
    val lapses: Int = 0,
    val priorityBoost: Int = 0,
    val lastReviewedAtEpochMillis: Long = 0,
)

object SpacedRepetition {
    private const val DAY_MILLIS = 24L * 60 * 60 * 1_000

    fun afterGuidedAnswer(
        current: ReviewState?,
        isCorrect: Boolean,
        nowEpochMillis: Long,
    ): ReviewState {
        val state = current ?: ReviewState()
        if (!isCorrect) {
            return state.copy(
                dueAtEpochMillis = nowEpochMillis,
                intervalDays = 0,
                stage = 0,
                lapses = state.lapses + 1,
                priorityBoost = max(state.priorityBoost, 4),
                lastReviewedAtEpochMillis = nowEpochMillis,
            )
        }

        val nextInterval = when (state.stage) {
            0 -> 1
            1 -> 3
            2 -> 7
            else -> max(14, state.intervalDays * 2)
        }
        return state.copy(
            dueAtEpochMillis = nowEpochMillis + nextInterval * DAY_MILLIS,
            intervalDays = nextInterval,
            stage = state.stage + 1,
            priorityBoost = (state.priorityBoost - 1).coerceAtLeast(0),
            lastReviewedAtEpochMillis = nowEpochMillis,
        )
    }

    fun afterFreeError(
        current: ReviewState?,
        nowEpochMillis: Long,
    ): ReviewState {
        val state = current ?: ReviewState()
        return state.copy(
            dueAtEpochMillis = nowEpochMillis,
            intervalDays = 0,
            stage = 0,
            lapses = state.lapses + 1,
            priorityBoost = max(state.priorityBoost + 2, 5),
            lastReviewedAtEpochMillis = nowEpochMillis,
        )
    }
}
