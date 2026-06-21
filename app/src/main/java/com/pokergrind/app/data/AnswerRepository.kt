package com.pokergrind.app.data

import com.pokergrind.app.data.local.AnswerDao
import com.pokergrind.app.data.local.AnswerEntity
import com.pokergrind.app.data.local.ProgressStore
import com.pokergrind.app.data.local.ReviewDao
import com.pokergrind.app.data.local.ReviewItemEntity
import com.pokergrind.app.data.local.SpotUnlockDao
import com.pokergrind.app.data.local.SpotUnlockEntity
import com.pokergrind.app.data.local.StoredQuestion
import com.pokergrind.app.data.local.StoredTrainingSession
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.training.AnswerSnapshot
import com.pokergrind.app.domain.training.ReviewState
import com.pokergrind.app.domain.training.SpacedRepetition
import com.pokergrind.app.domain.training.TrainingMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnswerRepository(
    private val answerDao: AnswerDao,
    private val reviewDao: ReviewDao,
    private val spotUnlockDao: SpotUnlockDao,
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

    fun observeUnlockedSpotIds(): Flow<Set<String>> =
        spotUnlockDao.observeUnlockedSpotIds().map(List<String>::toSet)

    fun observeFreeAnswerCount(): Flow<Int> = answerDao.observeFreeAnswerCount()

    suspend fun ensureUnlocked(spotId: String) {
        spotUnlockDao.unlock(
            SpotUnlockEntity(
                spotId = spotId,
                unlockedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun reviewStates(spotIds: List<String>): Map<Pair<String, String>, ReviewState> =
        reviewDao.getForSpots(spotIds).associate { entity ->
            (entity.spotId to entity.handNotation) to entity.toDomain()
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
        val now = System.currentTimeMillis()
        val insertedId = answerDao.insert(
            AnswerEntity(
                sessionId = session.id,
                questionIndex = session.questionIndex,
                mode = session.mode.name,
                spotId = spotId,
                handNotation = handNotation,
                expectedAction = expectedAction.name,
                chosenAction = chosenAction.name,
                isCorrect = isCorrect,
                responseTimeMillis = responseTimeMillis,
                answeredAtEpochMillis = now,
            ),
        )

        if (insertedId != -1L) {
            progressStore.answer(
                action = chosenAction,
                isCorrect = isCorrect,
            )
            updateReview(
                mode = session.mode,
                spotId = spotId,
                handNotation = handNotation,
                isCorrect = isCorrect,
                nowEpochMillis = now,
            )
            if (session.mode == TrainingMode.GUIDED && !isCorrect) {
                progressStore.scheduleRetry(
                    StoredQuestion(spotId = spotId, handNotation = handNotation),
                )
            }
        }
    }

    private suspend fun updateReview(
        mode: TrainingMode,
        spotId: String,
        handNotation: String,
        isCorrect: Boolean,
        nowEpochMillis: Long,
    ) {
        if (mode == TrainingMode.FREE && isCorrect) return
        val current = reviewDao.get(spotId, handNotation)?.toDomain()
        val updated = when (mode) {
            TrainingMode.GUIDED -> SpacedRepetition.afterGuidedAnswer(
                current = current,
                isCorrect = isCorrect,
                nowEpochMillis = nowEpochMillis,
            )

            TrainingMode.FREE -> SpacedRepetition.afterFreeError(
                current = current,
                nowEpochMillis = nowEpochMillis,
            )
        }
        reviewDao.upsert(updated.toEntity(spotId, handNotation))
    }
}

private fun ReviewItemEntity.toDomain() = ReviewState(
    dueAtEpochMillis = dueAtEpochMillis,
    intervalDays = intervalDays,
    stage = stage,
    lapses = lapses,
    priorityBoost = priorityBoost,
    lastReviewedAtEpochMillis = lastReviewedAtEpochMillis,
)

private fun ReviewState.toEntity(spotId: String, handNotation: String) = ReviewItemEntity(
    spotId = spotId,
    handNotation = handNotation,
    dueAtEpochMillis = dueAtEpochMillis,
    intervalDays = intervalDays,
    stage = stage,
    lapses = lapses,
    priorityBoost = priorityBoost,
    lastReviewedAtEpochMillis = lastReviewedAtEpochMillis,
)
