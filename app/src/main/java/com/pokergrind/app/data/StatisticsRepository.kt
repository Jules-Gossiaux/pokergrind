package com.pokergrind.app.data

import com.pokergrind.app.data.local.AnswerDao
import com.pokergrind.app.domain.statistics.HandStatistics
import com.pokergrind.app.domain.statistics.SpotStatistics
import com.pokergrind.app.domain.statistics.StatisticsSnapshot
import com.pokergrind.app.domain.training.TrainingMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class StatisticsRepository(answerDao: AnswerDao) {
    val statistics: Flow<StatisticsSnapshot> = combine(
        answerDao.observeSpotStats(TrainingMode.GUIDED.name),
        answerDao.observeSpotStats(TrainingMode.FREE.name),
        answerDao.observeHandStats(TrainingMode.GUIDED.name),
    ) { guidedSpots, freeSpots, guidedHands ->
        StatisticsSnapshot(
            guidedSpotStats = guidedSpots.map { row ->
                SpotStatistics(
                    spotId = row.spotId,
                    answerCount = row.answerCount,
                    correctCount = row.correctCount,
                    averageResponseTimeMillis = row.averageResponseTimeMillis,
                )
            },
            freeSpotStats = freeSpots.map { row ->
                SpotStatistics(
                    spotId = row.spotId,
                    answerCount = row.answerCount,
                    correctCount = row.correctCount,
                    averageResponseTimeMillis = row.averageResponseTimeMillis,
                )
            },
            guidedHandStats = guidedHands.map { row ->
                HandStatistics(
                    spotId = row.spotId,
                    handNotation = row.handNotation,
                    answerCount = row.answerCount,
                    correctCount = row.correctCount,
                    averageResponseTimeMillis = row.averageResponseTimeMillis,
                )
            },
        )
    }
}
