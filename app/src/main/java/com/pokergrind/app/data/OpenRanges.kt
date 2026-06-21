package com.pokergrind.app.data

import com.pokergrind.app.domain.model.RangeDefinition

object OpenRanges {
    val all: List<RangeDefinition> = listOf(
        BtnOpenRange.definition,
        CoOpenRange.definition,
        HjOpenRange.definition,
        UtgOpenRange.definition,
        SbOpenRange.definition,
    )
}
