package com.pokergrind.app.data

import com.pokergrind.app.data.local.AnswerDao
import com.pokergrind.app.data.local.AnswerEntity
import com.pokergrind.app.data.local.ProgressStore
import com.pokergrind.app.data.local.StoredTrainingSession
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.training.AnswerSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnswerRepository(
    private val answerDao: AnswerDao,
    private val progressStore: ProgressStore,
) {
    fun observeRecentAnswers(spotId: String, limit: Int): Flow<List<AnswerSnapshot>> =
        answerDao.observeRecent(spotId, limit).map { answers ->
            answers.map { answer ->
                AnswerSnapshot(
                    handNotation = answer.handNotation,
                    expectedAction = PokerAction.valueOf(answer.expectedAction),
                    isCorrect = answer.isCorrect,
                )
            }
        }

    suspend fun recordAnswer(
        session: StoredTrainingSession,
        spotId: String,
        handNotation: String,
        expectedAction: PokerAction,
        chosenAction: PokerAction,
        responseTimeMillis: Long,
    ) {
        val isCorrect = expectedAction == chosenAction
        val insertedId = answerDao.insert(
            AnswerEntity(
                sessionId = session.id,
                questionIndex = session.questionIndex,
                spotId = spotId,
                handNotation = handNotation,
                expectedAction = expectedAction.name,
                chosenAction = chosenAction.name,
                isCorrect = isCorrect,
                responseTimeMillis = responseTimeMillis,
                answeredAtEpochMillis = System.currentTimeMillis(),
            ),
        )

        if (insertedId != -1L) {
            progressStore.answer(
                action = chosenAction,
                isCorrect = isCorrect,
            )
        }
    }
}
