package com.pokergrind.app.domain.training

import com.pokergrind.app.domain.model.HandCategory
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import kotlin.random.Random

object SessionFactory {
    const val DAILY_SESSION_SIZE = 20

    fun createBalancedSession(
        range: RangeDefinition,
        random: Random = Random.Default,
    ): List<HandCategory> {
        val byAction = range.entries.groupBy { it.action }
        val actions = range.availableActions
        val perAction = (DAILY_SESSION_SIZE / actions.size).coerceAtLeast(1)
        val remainder = DAILY_SESSION_SIZE - perAction * actions.size

        return buildList {
            actions.forEachIndexed { index, action ->
                val target = perAction + if (index < remainder) 1 else 0
                addAll(byAction.getValue(action).shuffled(random).take(target))
            }
        }
            .shuffled(random)
            .map { it.hand }
    }
}
