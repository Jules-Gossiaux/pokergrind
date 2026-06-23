package com.pokergrind.app.data

import com.pokergrind.app.domain.model.HandMatrix
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeChapter
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.model.RangeEntry

object BbDefenseRanges {
    val vsBtn = bbDefenseRange(
        id = "bb_vs_btn_defense_100bb_v1",
        title = "BB vs BTN",
        opener = "BTN",
        threeBetHands = setOf(
            "AA", "KK", "QQ", "JJ", "TT",
            "AKs", "AKo", "AQs", "AQo",
            "AJs",
            "KQs",
        ),
        callHands = setOf(
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
        ),
    )

    val vsCo = bbDefenseRange(
        id = "bb_vs_co_defense_100bb_v1",
        title = "BB vs CO",
        opener = "CO",
        threeBetHands = setOf(
            "AA", "KK", "QQ", "JJ", "TT",
            "AKs", "AKo",
            "AQs", "AJs",
            "KQs",
        ),
        callHands = setOf(
            "99", "88", "77", "66", "55", "44", "33", "22",
            "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s",
            "KJs", "KTs",
            "QJs", "QTs",
            "JTs", "J9s",
            "T9s",
            "98s",
            "87s",
            "76s",
            "AQo", "AJo",
            "KQo",
        ),
    )

    val vsSb = bbDefenseRange(
        id = "bb_vs_sb_defense_100bb_v1",
        title = "BB vs SB",
        opener = "SB",
        threeBetHands = setOf(
            "AA", "KK", "QQ", "JJ", "TT", "99",
            "AKs", "AKo",
            "AQs", "AQo",
            "AJs", "AJo",
            "A5s", "A4s", "A3s", "A2s",
            "KQs", "KJs",
        ),
        callHands = setOf(
            "88", "77", "66", "55", "44", "33", "22",
            "ATs", "A9s", "A8s", "A7s", "A6s",
            "KTs", "K9s", "K8s", "K7s", "K6s", "K5s", "K4s", "K3s", "K2s",
            "QTs", "Q9s", "Q8s", "Q7s", "Q6s", "Q5s",
            "JTs", "J9s", "J8s", "J7s",
            "T9s", "T8s", "T7s",
            "98s", "87s", "86s",
            "76s", "65s", "54s",
            "ATo", "A9o", "A8o", "A7o", "A6o", "A5o", "A4o", "A3o", "A2o",
            "KQo", "KJo", "KTo",
            "QJo", "QTo",
            "JTo",
        ),
    )

    val vsHj = bbDefenseRange(
        id = "bb_vs_hj_defense_100bb_v1",
        title = "BB vs HJ",
        opener = "HJ",
        threeBetHands = setOf(
            "AA", "KK", "QQ",
            "AKs", "AKo",
            "AQs",
        ),
        callHands = setOf(
            "JJ", "TT", "99", "88", "77", "66", "55", "44", "33", "22",
            "AJs", "ATs",
            "KQs", "KJs",
            "QJs",
            "JTs",
            "T9s",
            "98s",
            "87s",
            "AQo",
        ),
    )

    val vsUtg = bbDefenseRange(
        id = "bb_vs_utg_defense_100bb_v1",
        title = "BB vs UTG",
        opener = "UTG",
        threeBetHands = setOf(
            "AA", "KK", "QQ",
            "AKs", "AKo",
        ),
        callHands = setOf(
            "JJ", "TT", "99", "88", "77", "66", "55", "44", "33", "22",
            "AQs", "AJs",
            "KQs",
            "QJs",
            "JTs",
            "T9s",
            "98s",
            "AQo",
        ),
    )

    val all: List<RangeDefinition> = listOf(
        vsBtn,
        vsCo,
        vsSb,
        vsHj,
        vsUtg,
    )

    private fun bbDefenseRange(
        id: String,
        title: String,
        opener: String,
        threeBetHands: Set<String>,
        callHands: Set<String>,
    ) = RangeDefinition(
        id = id,
        title = title,
        position = "BB",
        stackDepthBb = 100,
        sizingBb = 2.5,
        chapter = RangeChapter.BB_DEFENSES,
        spotContext = "$opener ouvre à 2,5 BB",
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
