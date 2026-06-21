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

        val perAction = SessionFactory.DAILY_SESSION_SIZE / 2
        return PokerAction.entries.flatMap { action ->
            candidates
                .filter { it.action == action }
                .shuffled(random)
                .sortedByDescending { priority(it.review, nowEpochMillis) }
                .take(perAction)
        }
            .shuffled(random)
            .map { it.spotId to it.hand }
    }

    private fun priority(review: ReviewState?, nowEpochMillis: Long): Long {
        if (review == null) return 2_000_000_000L
        val overdueMillis = (nowEpochMillis - review.dueAtEpochMillis).coerceAtLeast(0)
        val dueScore = if (review.dueAtEpochMillis <= nowEpochMillis) 1_000_000_000L else 0
        return review.priorityBoost * 10_000_000_000L +
            dueScore +
            overdueMillis / 1_000 +
            (10 - review.stage).coerceAtLeast(0) * 100_000L
    }
}
