package com.pokergrind.app.data

import com.pokergrind.app.domain.model.HandMatrix
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.model.RangeEntry

object SbOpenRange {
    private val openHands = setOf(
        "AA", "AKs", "AQs", "AJs", "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s",
        "AKo", "KK", "KQs", "KJs", "KTs", "K9s", "K8s",
        "AQo", "KQo", "QQ", "QJs", "QTs", "Q9s", "Q8s",
        "AJo", "KJo", "QJo", "JJ", "JTs", "J9s", "J8s",
        "ATo", "KTo", "QTo", "JTo", "TT", "T9s", "T8s",
        "A9o", "K9o", "Q9o", "J9o", "T9o", "99", "98s", "97s", "96s",
        "A8o", "98o", "88", "87s", "86s",
        "77", "76s", "75s",
        "66", "65s",
        "55", "44", "33", "22",
    )

    val definition = RangeDefinition(
        id = "open_sb_100bb_v1",
        title = "Open SB",
        position = "SB",
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
