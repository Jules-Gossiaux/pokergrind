package com.pokergrind.app.domain.model

object HandMatrix {
    val ranks: List<Char> = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')

    val allHands: List<HandCategory> = buildList {
        ranks.forEachIndexed { row, rowRank ->
            ranks.forEachIndexed { column, columnRank ->
                add(
                    when {
                        row == column -> HandCategory(
                            notation = "$rowRank$columnRank",
                            highRank = rowRank,
                            lowRank = columnRank,
                            shape = HandShape.PAIR,
                        )

                        row < column -> HandCategory(
                            notation = "$rowRank${columnRank}s",
                            highRank = rowRank,
                            lowRank = columnRank,
                            shape = HandShape.SUITED,
                        )

                        else -> HandCategory(
                            notation = "$columnRank${rowRank}o",
                            highRank = columnRank,
                            lowRank = rowRank,
                            shape = HandShape.OFFSUIT,
                        )
                    },
                )
            }
        }
    }
}
