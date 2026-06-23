package com.pokergrind.app.domain.training

import com.pokergrind.app.domain.model.HandCategory
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import kotlin.random.Random

data class ReviewCandidate(
    val spotId: String,
    val hand: HandCategory,
    val action: PokerAction,
    val review: ReviewState?,
)

object GuidedSessionPlanner {
    fun plan(
        ranges: List<RangeDefinition>,
        reviewStates: Map<Pair<String, String>, ReviewState>,
        nowEpochMillis: Long,
        random: Random = Random.Default,
    ): List<Pair<String, HandCategory>> {
        val candidates = ranges.flatMap { range ->
            range.entries.map { entry ->
                ReviewCandidate(
                    spotId = range.id,
                    hand = entry.hand,
                    action = entry.action,
                    review = reviewStates[range.id to entry.hand.notation],
                )
            }
        }

        val selected = mutableListOf<ReviewCandidate>()
        val minimumPerSpot = (SessionFactory.DAILY_SESSION_SIZE / ranges.size)
            .coerceAtMost(MINIMUM_PER_SPOT)
        ranges.forEach { range ->
            val minimumPerAction = (minimumPerSpot / range.availableActions.size).coerceAtLeast(1)
            range.availableActions.forEach { action ->
                selected += candidates
                    .filter { it.spotId == range.id && it.action == action }
                    .shuffled(random)
                    .sortedByDescending { priority(it.review, nowEpochMillis) }
                    .take(minimumPerAction)
            }
        }

        val selectedKeys = selected.map { it.spotId to it.hand.notation }.toMutableSet()
        val allActions = ranges.flatMap { it.availableActions }.distinct()
        val targetPerAction = (SessionFactory.DAILY_SESSION_SIZE / allActions.size).coerceAtLeast(1)
        allActions.forEach { action ->
            val remainingForAction = targetPerAction - selected.count { it.action == action }
            if (remainingForAction <= 0) return@forEach
            val additions = candidates
                .filter { it.action == action && (it.spotId to it.hand.notation) !in selectedKeys }
                .shuffled(random)
                .sortedByDescending { priority(it.review, nowEpochMillis) }
                .take(remainingForAction)
            selected += additions
            selectedKeys += additions.map { it.spotId to it.hand.notation }
        }

        return selected
            .distinctBy { it.spotId to it.hand.notation }
            .sortedByDescending { priority(it.review, nowEpochMillis) }
            .take(SessionFactory.DAILY_SESSION_SIZE)
            .shuffled(random)
            .map { it.spotId to it.hand }
    }

    private fun priority(review: ReviewState?, nowEpochMillis: Long): Long {
        if (review == null) return 500_000_000L
        val overdueMillis = (nowEpochMillis - review.dueAtEpochMillis).coerceAtLeast(0)
        val dueScore = if (review.dueAtEpochMillis <= nowEpochMillis) 1_000_000_000L else 0
        return review.priorityBoost * 10_000_000_000L +
            dueScore +
            overdueMillis / 1_000 +
            (10 - review.stage).coerceAtLeast(0) * 100_000L
    }

    private const val MINIMUM_PER_SPOT = 4
}
