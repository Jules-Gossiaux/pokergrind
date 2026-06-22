package com.pokergrind.app.data

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.pokergrind.app.data.local.AnswerEntity
import com.pokergrind.app.data.local.PokerGrindDatabase
import com.pokergrind.app.data.local.ProgressStore
import com.pokergrind.app.data.local.ReviewItemEntity
import com.pokergrind.app.data.local.SpotUnlockEntity
import com.pokergrind.app.data.local.StoredProgress
import com.pokergrind.app.data.local.StoredQuestion
import com.pokergrind.app.data.local.StoredTrainingSession
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.training.TrainingMode
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class BackupManager(
    private val context: Context,
    private val database: PokerGrindDatabase,
    private val progressStore: ProgressStore,
) {
    suspend fun exportTo(uri: Uri) = withContext(Dispatchers.IO) {
        val root = JSONObject()
            .put("formatVersion", FORMAT_VERSION)
            .put("exportedAtEpochMillis", System.currentTimeMillis())
            .put("progress", progressStore.progress.first().toJson())
            .put("answers", database.answerDao().getAll().answersToJson())
            .put("reviews", database.reviewDao().getAll().reviewsToJson())
            .put("unlocks", database.spotUnlockDao().getAll().unlocksToJson())

        context.contentResolver.openOutputStream(uri, "wt")?.bufferedWriter()?.use {
            it.write(root.toString())
        } ?: error("Impossible d'ouvrir le fichier de sauvegarde.")
    }

    suspend fun importFrom(uri: Uri) = withContext(Dispatchers.IO) {
        val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            ?: error("Impossible de lire le fichier de sauvegarde.")
        val root = JSONObject(content)
        require(root.getInt("formatVersion") == FORMAT_VERSION) {
            "Version de sauvegarde non prise en charge."
        }

        val answers = root.getJSONArray("answers").toAnswers()
        val reviews = root.getJSONArray("reviews").toReviews()
        val unlocks = root.getJSONArray("unlocks").toUnlocks()
        val progress = root.getJSONObject("progress").toProgress()

        database.withTransaction {
            database.answerDao().deleteAll()
            database.reviewDao().deleteAll()
            database.spotUnlockDao().deleteAll()
            database.answerDao().insertAll(answers)
            database.reviewDao().upsertAll(reviews)
            database.spotUnlockDao().unlockAll(unlocks)
        }
        progressStore.restore(progress)
    }

    private fun StoredProgress.toJson() = JSONObject()
        .put("xp", xp)
        .put("streak", streak)
        .put("lastCompletedDate", lastCompletedDate?.toString())
        .put("guidedSession", guidedSession?.toJson())
        .put("freeSession", freeSession?.toJson())

    private fun StoredTrainingSession.toJson() = JSONObject()
        .put("id", id)
        .put("mode", mode.name)
        .put("questionIndex", questionIndex)
        .put("correctCount", correctCount)
        .put("selectedAction", selectedAction?.name)
        .put(
            "questions",
            JSONArray().apply {
                questions.forEach { question ->
                    put(
                        JSONObject()
                            .put("spotId", question.spotId)
                            .put("handNotation", question.handNotation),
                    )
                }
            },
        )

    private fun List<AnswerEntity>.answersToJson() = JSONArray().apply {
        forEach { answer ->
            put(
                JSONObject()
                    .put("sessionId", answer.sessionId)
                    .put("questionIndex", answer.questionIndex)
                    .put("mode", answer.mode)
                    .put("spotId", answer.spotId)
                    .put("handNotation", answer.handNotation)
                    .put("expectedAction", answer.expectedAction)
                    .put("chosenAction", answer.chosenAction)
                    .put("isCorrect", answer.isCorrect)
                    .put("responseTimeMillis", answer.responseTimeMillis)
                    .put("answeredAtEpochMillis", answer.answeredAtEpochMillis),
            )
        }
    }

    private fun List<ReviewItemEntity>.reviewsToJson() = JSONArray().apply {
        forEach { review ->
            put(
                JSONObject()
                    .put("spotId", review.spotId)
                    .put("handNotation", review.handNotation)
                    .put("dueAtEpochMillis", review.dueAtEpochMillis)
                    .put("intervalDays", review.intervalDays)
                    .put("stage", review.stage)
                    .put("lapses", review.lapses)
                    .put("priorityBoost", review.priorityBoost)
                    .put("lastReviewedAtEpochMillis", review.lastReviewedAtEpochMillis),
            )
        }
    }

    private fun List<SpotUnlockEntity>.unlocksToJson() = JSONArray().apply {
        forEach { unlock ->
            put(
                JSONObject()
                    .put("spotId", unlock.spotId)
                    .put("unlockedAtEpochMillis", unlock.unlockedAtEpochMillis),
            )
        }
    }

    private fun JSONObject.toProgress() = StoredProgress(
        xp = getInt("xp"),
        streak = getInt("streak"),
        lastCompletedDate = optString("lastCompletedDate").takeIf(String::isNotBlank)?.let(LocalDate::parse),
        guidedSession = optJSONObject("guidedSession")?.toSession(),
        freeSession = optJSONObject("freeSession")?.toSession(),
    )

    private fun JSONObject.toSession() = StoredTrainingSession(
        id = getString("id"),
        mode = TrainingMode.valueOf(getString("mode")),
        questions = getJSONArray("questions").let { questions ->
            List(questions.length()) { index ->
                val question = questions.getJSONObject(index)
                StoredQuestion(
                    spotId = question.getString("spotId"),
                    handNotation = question.getString("handNotation"),
                )
            }
        },
        questionIndex = getInt("questionIndex"),
        correctCount = getInt("correctCount"),
        selectedAction = optString("selectedAction")
            .takeIf(String::isNotBlank)
            ?.let(PokerAction::valueOf),
    )

    private fun JSONArray.toAnswers() = List(length()) { index ->
        getJSONObject(index).let { answer ->
            AnswerEntity(
                sessionId = answer.getString("sessionId"),
                questionIndex = answer.getInt("questionIndex"),
                mode = answer.getString("mode"),
                spotId = answer.getString("spotId"),
                handNotation = answer.getString("handNotation"),
                expectedAction = answer.getString("expectedAction"),
                chosenAction = answer.getString("chosenAction"),
                isCorrect = answer.getBoolean("isCorrect"),
                responseTimeMillis = answer.getLong("responseTimeMillis"),
                answeredAtEpochMillis = answer.getLong("answeredAtEpochMillis"),
            )
        }
    }

    private fun JSONArray.toReviews() = List(length()) { index ->
        getJSONObject(index).let { review ->
            ReviewItemEntity(
                spotId = review.getString("spotId"),
                handNotation = review.getString("handNotation"),
                dueAtEpochMillis = review.getLong("dueAtEpochMillis"),
                intervalDays = review.getInt("intervalDays"),
                stage = review.getInt("stage"),
                lapses = review.getInt("lapses"),
                priorityBoost = review.getInt("priorityBoost"),
                lastReviewedAtEpochMillis = review.getLong("lastReviewedAtEpochMillis"),
            )
        }
    }

    private fun JSONArray.toUnlocks() = List(length()) { index ->
        getJSONObject(index).let { unlock ->
            SpotUnlockEntity(
                spotId = unlock.getString("spotId"),
                unlockedAtEpochMillis = unlock.getLong("unlockedAtEpochMillis"),
            )
        }
    }

    companion object {
        private const val FORMAT_VERSION = 1
    }
}
