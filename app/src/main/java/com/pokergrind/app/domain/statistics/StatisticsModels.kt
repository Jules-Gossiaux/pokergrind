package com.pokergrind.app.domain.statistics

data class SpotStatistics(
    val spotId: String,
    val answerCount: Int,
    val correctCount: Int,
    val averageResponseTimeMillis: Long,
) {
    val successRatePercent: Int
        get() = if (answerCount == 0) 0 else correctCount * 100 / answerCount
}

data class HandStatistics(
    val spotId: String,
    val handNotation: String,
    val answerCount: Int,
    val correctCount: Int,
    val averageResponseTimeMillis: Long,
) {
    val successRatePercent: Int
        get() = if (answerCount == 0) 0 else correctCount * 100 / answerCount
}

data class StatisticsSnapshot(
    val guidedSpotStats: List<SpotStatistics> = emptyList(),
    val freeSpotStats: List<SpotStatistics> = emptyList(),
    val guidedHandStats: List<HandStatistics> = emptyList(),
)
