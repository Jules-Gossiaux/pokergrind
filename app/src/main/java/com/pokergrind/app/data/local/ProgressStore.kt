package com.pokergrind.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.training.ProgressionRules
import com.pokergrind.app.domain.training.TrainingMode
import java.io.IOException
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.progressDataStore by preferencesDataStore(name = "pokergrind_progress")

data class StoredQuestion(
    val spotId: String,
    val handNotation: String,
)

data class StoredTrainingSession(
    val id: String,
    val mode: TrainingMode,
    val questions: List<StoredQuestion>,
    val questionIndex: Int,
    val correctCount: Int,
    val selectedAction: PokerAction?,
) {
    val isComplete: Boolean
        get() = questions.isNotEmpty() && questionIndex >= questions.size

    val currentQuestion: StoredQuestion?
        get() = questions.getOrNull(questionIndex)
}

data class StoredProgress(
    val xp: Int = 0,
    val streak: Int = 0,
    val lastCompletedDate: LocalDate? = null,
    val session: StoredTrainingSession? = null,
)

class ProgressStore(private val context: Context) {
    val progress: Flow<StoredProgress> = context.progressDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw exception
        }
        .map(::toStoredProgress)

    suspend fun startSession(
        mode: TrainingMode,
        questions: List<StoredQuestion>,
    ) {
        context.progressDataStore.edit { preferences ->
            preferences[SESSION_ID] = UUID.randomUUID().toString()
            preferences[SESSION_MODE] = mode.name
            preferences[SESSION_QUESTIONS] = encodeQuestions(questions)
            preferences[SESSION_INDEX] = 0
            preferences[SESSION_CORRECT] = 0
            preferences.remove(SESSION_SELECTED_ACTION)
        }
    }

    suspend fun answer(action: PokerAction, isCorrect: Boolean) {
        context.progressDataStore.edit { preferences ->
            if (preferences[SESSION_SELECTED_ACTION] != null) return@edit
            preferences[SESSION_SELECTED_ACTION] = action.name
            if (isCorrect) {
                preferences[SESSION_CORRECT] = (preferences[SESSION_CORRECT] ?: 0) + 1
                preferences[XP] = (preferences[XP] ?: 0) + XP_PER_CORRECT_ANSWER
            }
        }
    }

    suspend fun moveToNextQuestion() {
        context.progressDataStore.edit { preferences ->
            val questions = decodeQuestions(
                preferences[SESSION_QUESTIONS] ?: preferences[LEGACY_SESSION_HANDS],
            )
            val nextIndex = (preferences[SESSION_INDEX] ?: 0) + 1
            preferences[SESSION_INDEX] = nextIndex
            preferences.remove(SESSION_SELECTED_ACTION)

            val mode = preferences[SESSION_MODE]
                ?.let { runCatching { TrainingMode.valueOf(it) }.getOrNull() }
                ?: TrainingMode.GUIDED
            if (mode == TrainingMode.GUIDED && questions.isNotEmpty() && nextIndex >= questions.size) {
                updateStreak(preferences, LocalDate.now())
            }
        }
    }

    suspend fun scheduleRetry(question: StoredQuestion) {
        context.progressDataStore.edit { preferences ->
            val questions = decodeQuestions(
                preferences[SESSION_QUESTIONS] ?: preferences[LEGACY_SESSION_HANDS],
            ).toMutableList()
            val currentIndex = preferences[SESSION_INDEX] ?: 0
            if (questions.isEmpty() || currentIndex >= questions.lastIndex) return@edit

            val insertionIndex = (currentIndex + RETRY_GAP + 1).coerceAtMost(questions.lastIndex)
            val alreadySoon = questions
                .subList(currentIndex + 1, insertionIndex + 1)
                .any { it == question }
            if (alreadySoon) return@edit

            questions.add(insertionIndex, question)
            if (questions.size > SESSION_SIZE) questions.removeAt(questions.lastIndex)
            preferences[SESSION_QUESTIONS] = encodeQuestions(questions)
        }
    }

    private fun toStoredProgress(preferences: Preferences): StoredProgress {
        val questions = decodeQuestions(
            preferences[SESSION_QUESTIONS] ?: preferences[LEGACY_SESSION_HANDS],
        )
        val session = questions.takeIf { it.isNotEmpty() }?.let {
            StoredTrainingSession(
                id = preferences[SESSION_ID] ?: "legacy-session",
                mode = preferences[SESSION_MODE]
                    ?.let { value -> runCatching { TrainingMode.valueOf(value) }.getOrNull() }
                    ?: TrainingMode.GUIDED,
                questions = it,
                questionIndex = preferences[SESSION_INDEX] ?: 0,
                correctCount = preferences[SESSION_CORRECT] ?: 0,
                selectedAction = preferences[SESSION_SELECTED_ACTION]
                    ?.let { runCatching { PokerAction.valueOf(it) }.getOrNull() },
            )
        }

        return StoredProgress(
            xp = preferences[XP] ?: 0,
            streak = ProgressionRules.visibleStreak(
                storedStreak = preferences[STREAK] ?: 0,
                lastCompletedDate = preferences[LAST_COMPLETED_DATE]?.let(LocalDate::parse),
                today = LocalDate.now(),
            ),
            lastCompletedDate = preferences[LAST_COMPLETED_DATE]?.let(LocalDate::parse),
            session = session,
        )
    }

    private fun updateStreak(preferences: androidx.datastore.preferences.core.MutablePreferences, today: LocalDate) {
        val lastCompleted = preferences[LAST_COMPLETED_DATE]?.let(LocalDate::parse)
        if (lastCompleted == today) return

        preferences[STREAK] = ProgressionRules.nextStreak(
            currentStreak = preferences[STREAK] ?: 0,
            lastCompletedDate = lastCompleted,
            completedDate = today,
        )
        preferences[LAST_COMPLETED_DATE] = today.toString()
    }

    private fun encodeQuestions(questions: List<StoredQuestion>): String =
        questions.joinToString(";") { "${it.spotId}|${it.handNotation}" }

    private fun decodeQuestions(value: String?): List<StoredQuestion> =
        value
            ?.split(";", ",")
            ?.mapNotNull { encoded ->
                if (encoded.isBlank()) return@mapNotNull null
                val parts = encoded.split("|", limit = 2)
                if (parts.size == 2) {
                    StoredQuestion(parts[0], parts[1])
                } else {
                    StoredQuestion(LEGACY_BTN_SPOT_ID, encoded)
                }
            }
            .orEmpty()

    companion object {
        const val XP_PER_CORRECT_ANSWER = 10
        private const val SESSION_SIZE = 20
        private const val RETRY_GAP = 3
        private const val LEGACY_BTN_SPOT_ID = "open_btn_100bb_v1"

        private val XP = intPreferencesKey("xp")
        private val STREAK = intPreferencesKey("streak")
        private val LAST_COMPLETED_DATE = stringPreferencesKey("last_completed_date")
        private val LEGACY_SESSION_HANDS = stringPreferencesKey("session_hands")
        private val SESSION_QUESTIONS = stringPreferencesKey("session_questions")
        private val SESSION_MODE = stringPreferencesKey("session_mode")
        private val SESSION_ID = stringPreferencesKey("session_id")
        private val SESSION_INDEX = intPreferencesKey("session_index")
        private val SESSION_CORRECT = intPreferencesKey("session_correct")
        private val SESSION_SELECTED_ACTION = stringPreferencesKey("session_selected_action")
    }
}
