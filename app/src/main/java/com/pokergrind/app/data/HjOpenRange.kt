package com.pokergrind.app.data

import com.pokergrind.app.domain.model.HandMatrix
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.model.RangeEntry

object HjOpenRange {
    private val openHands = setOf(
        "AA", "AKs", "AQs", "AJs", "ATs",
        "AKo", "KK", "KQs", "KJs", "KTs",
        "AQo", "KQo", "QQ", "QJs", "QTs",
        "AJo", "KJo", "QJo", "JJ", "JTs",
        "TT", "99", "88", "77", "66", "55", "44", "33", "22",
    )

    val definition = RangeDefinition(
        id = "open_hj_100bb_v1",
        title = "Open HJ",
        position = "HJ",
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
