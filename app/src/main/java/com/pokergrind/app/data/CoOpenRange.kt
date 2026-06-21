package com.pokergrind.app.data

import com.pokergrind.app.domain.model.HandMatrix
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.model.RangeEntry

object CoOpenRange {
    private val openHands = setOf(
        "AA", "AKs", "AQs", "AJs", "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s",
        "AKo", "KK", "KQs", "KJs", "KTs", "K9s",
        "AQo", "KQo", "QQ", "QJs", "QTs", "Q9s",
        "AJo", "KJo", "QJo", "JJ", "JTs", "J9s",
        "ATo", "KTo", "QTo", "JTo", "TT", "T9s",
        "99", "98s",
        "88", "87s",
        "77", "76s",
        "66", "65s",
        "55",
        "44",
        "33",
        "22",
    )

    val definition = RangeDefinition(
        id = "open_co_100bb_v1",
        title = "Open CO",
        position = "CO",
        stackDepthBb = 100,
        sizingBb = 2.5,
        entries = HandMatrix.allHands.map { hand ->
            RangeEntry(
                hand = hand,
                action = if (hand.notation in openHands) PokerAction.OPEN else PokerAction.FOLD,
            )
        },
    )
}
