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
        val perAction = DAILY_SESSION_SIZE / 2

        return buildList {
            addAll(byAction.getValue(PokerAction.OPEN).shuffled(random).take(perAction))
            addAll(byAction.getValue(PokerAction.FOLD).shuffled(random).take(perAction))
        }
            .shuffled(random)
            .map { it.hand }
    }
}
