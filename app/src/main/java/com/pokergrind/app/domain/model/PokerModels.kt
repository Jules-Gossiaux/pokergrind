package com.pokergrind.app.domain.model

enum class PokerAction(val label: String) {
    OPEN("Open"),
    CALL("Call"),
    THREE_BET("3-bet"),
    FOLD("Fold"),
}

enum class HandShape {
    PAIR,
    SUITED,
    OFFSUIT,
}

data class HandCategory(
    val notation: String,
    val highRank: Char,
    val lowRank: Char,
    val shape: HandShape,
) {
    val comboCount: Int
        get() = when (shape) {
            HandShape.PAIR -> 6
            HandShape.SUITED -> 4
            HandShape.OFFSUIT -> 12
        }
}

data class RangeEntry(
    val hand: HandCategory,
    val action: PokerAction,
)

data class RangeDefinition(
    val id: String,
    val title: String,
    val position: String,
    val stackDepthBb: Int,
    val sizingBb: Double,
    val entries: List<RangeEntry>,
    val chapter: RangeChapter = RangeChapter.OPENS,
    val spotContext: String = "À toi de parler",
    val decisionPrompt: String = "100 BB",
    val actionOrder: List<PokerAction> = listOf(PokerAction.FOLD, PokerAction.OPEN),
) {
    init {
        require(entries.size == 169) { "Une range doit contenir exactement 169 catégories." }
        require(entries.map { it.hand.notation }.distinct().size == 169) {
            "Chaque catégorie de main doit être unique."
        }
    }

    val openComboCount: Int
        get() = entries
            .filter { it.action == PokerAction.OPEN }
            .sumOf { it.hand.comboCount }

    val openPercentage: Double
        get() = openComboCount.toDouble() / TOTAL_PREFLOP_COMBOS * 100

    fun actionFor(hand: HandCategory): PokerAction =
        entries.first { it.hand.notation == hand.notation }.action

    fun comboCountFor(action: PokerAction): Int =
        entries
            .filter { it.action == action }
            .sumOf { it.hand.comboCount }

    fun percentageFor(action: PokerAction): Double =
        comboCountFor(action).toDouble() / TOTAL_PREFLOP_COMBOS * 100

    val availableActions: List<PokerAction>
        get() = actionOrder.filter { action -> entries.any { it.action == action } }

    companion object {
        const val TOTAL_PREFLOP_COMBOS = 1326
    }
}

enum class RangeChapter {
    OPENS,
    BB_DEFENSES,
    THREE_BETS,
}
