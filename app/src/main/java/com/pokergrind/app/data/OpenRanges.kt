package com.pokergrind.app.data

import com.pokergrind.app.domain.model.RangeDefinition

object OpenRanges {
    val open: List<RangeDefinition> = listOf(
        BtnOpenRange.definition,
        CoOpenRange.definition,
        HjOpenRange.definition,
        UtgOpenRange.definition,
        SbOpenRange.definition,
    )

    val bbDefense: List<RangeDefinition> = listOf(
        BbVsBtnDefenseRange.definition,
    )

    val all: List<RangeDefinition> = open + bbDefense
}
