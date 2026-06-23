package com.pokergrind.app.data

import com.pokergrind.app.domain.model.HandMatrix
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeChapter
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.model.RangeEntry

object BbVsBtnDefenseRange {
    private val threeBetHands = setOf(
        "AA", "KK", "QQ", "JJ", "TT",
        "AKs", "AKo", "AQs", "AQo",
        "AJs",
        "KQs",
    )

    private val callHands = setOf(
        "99", "88", "77", "66", "55", "44", "33", "22",
        "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s",
        "KJs", "KTs", "K9s",
        "QJs", "QTs", "Q9s",
        "JTs", "J9s", "J8s",
        "T9s", "T8s",
        "98s", "87s", "86s",
        "76s", "65s",
        "AJo", "ATo",
        "KQo", "KJo",
        "QJo",
    )

    val definition = RangeDefinition(
        id = "bb_vs_btn_defense_100bb_v1",
        title = "BB vs BTN",
        position = "BB",
        stackDepthBb = 100,
        sizingBb = 2.5,
        chapter = RangeChapter.BB_DEFENSES,
        spotContext = "BTN ouvre à 2,5 BB",
        decisionPrompt = "Tu es BB · 100 BB",
        actionOrder = listOf(PokerAction.FOLD, PokerAction.CALL, PokerAction.THREE_BET),
        entries = HandMatrix.allHands.map { hand ->
            RangeEntry(
                hand = hand,
                action = when (hand.notation) {
                    in threeBetHands -> PokerAction.THREE_BET
                    in callHands -> PokerAction.CALL
                    else -> PokerAction.FOLD
                },
            )
        },
    )
}
