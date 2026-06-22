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
    val guidedSession: StoredTrainingSession? = null,
    val freeSession: StoredTrainingSession? = null,
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
            val keys = keysFor(mode)
            preferences[keys.id] = UUID.randomUUID().toString()
            preferences[keys.questions] = encodeQuestions(questions)
            preferences[keys.index] = 0
            preferences[keys.correct] = 0
            preferences.remove(keys.selectedAction)
        }
    }

    suspend fun restore(progress: StoredProgress) {
        context.progressDataStore.edit { preferences ->
            preferences.clear()
            preferences[XP] = progress.xp
            preferences[STREAK] = progress.streak
            progress.lastCompletedDate?.let { preferences[LAST_COMPLETED_DATE] = it.toString() }
            writeSession(preferences, progress.guidedSession)
            writeSession(preferences, progress.freeSession)
        }
    }

    suspend fun discardSession(mode: TrainingMode) {
        context.progressDataStore.edit { preferences ->
            val keys = keysFor(mode)
            preferences.remove(keys.id)
            preferences.remove(keys.questions)
            preferences.remove(keys.index)
            preferences.remove(keys.correct)
            preferences.remove(keys.selectedAction)
            if (legacyMode(preferences) == mode) clearLegacySession(preferences)
        }
    }

    suspend fun answer(mode: TrainingMode, action: PokerAction, isCorrect: Boolean) {
        context.progressDataStore.edit { preferences ->
            val keys = activeKeys(preferences, mode)
            if (preferences[keys.selectedAction] != null) return@edit
            preferences[keys.selectedAction] = action.name
            if (isCorrect) {
                preferences[keys.correct] = (preferences[keys.correct] ?: 0) + 1
                preferences[XP] = (preferences[XP] ?: 0) + XP_PER_CORRECT_ANSWER
            }
        }
    }

    suspend fun moveToNextQuestion(mode: TrainingMode) {
        context.progressDataStore.edit { preferences ->
            val keys = activeKeys(preferences, mode)
            val questions = decodeQuestions(
                preferences[keys.questions],
            )
            val nextIndex = (preferences[keys.index] ?: 0) + 1
            preferences[keys.index] = nextIndex
            preferences.remove(keys.selectedAction)

            if (mode == TrainingMode.GUIDED && questions.isNotEmpty() && nextIndex >= questions.size) {
                updateStreak(preferences, LocalDate.now())
            }
        }
    }

    suspend fun scheduleRetry(mode: TrainingMode, question: StoredQuestion) {
        context.progressDataStore.edit { preferences ->
            val keys = activeKeys(preferences, mode)
            val questions = decodeQuestions(
                preferences[keys.questions],
            ).toMutableList()
            val currentIndex = preferences[keys.index] ?: 0
            if (questions.isEmpty() || currentIndex >= questions.lastIndex) return@edit

            val insertionIndex = (currentIndex + RETRY_GAP + 1).coerceAtMost(questions.lastIndex)
            val alreadySoon = questions
                .subList(currentIndex + 1, insertionIndex + 1)
                .any { it == question }
            if (alreadySoon) return@edit

            questions.add(insertionIndex, question)
            if (questions.size > SESSION_SIZE) questions.removeAt(questions.lastIndex)
            preferences[keys.questions] = encodeQuestions(questions)
        }
    }

    private fun toStoredProgress(preferences: Preferences): StoredProgress {
        return StoredProgress(
            xp = preferences[XP] ?: 0,
            streak = ProgressionRules.visibleStreak(
                storedStreak = preferences[STREAK] ?: 0,
                lastCompletedDate = preferences[LAST_COMPLETED_DATE]?.let(LocalDate::parse),
                today = LocalDate.now(),
            ),
            lastCompletedDate = preferences[LAST_COMPLETED_DATE]?.let(LocalDate::parse),
            guidedSession = sessionFor(preferences, TrainingMode.GUIDED),
            freeSession = sessionFor(preferences, TrainingMode.FREE),
        )
    }

    private fun sessionFor(
        preferences: Preferences,
        mode: TrainingMode,
    ): StoredTrainingSession? {
        val dedicatedKeys = keysFor(mode)
        val keys = when {
            preferences[dedicatedKeys.questions] != null -> dedicatedKeys
            legacyMode(preferences) == mode -> LEGACY_KEYS
            else -> return null
        }
        val questions = decodeQuestions(preferences[keys.questions])
        return questions.takeIf { it.isNotEmpty() }?.let {
            StoredTrainingSession(
                id = preferences[keys.id] ?: "legacy-${mode.name.lowercase()}-session",
                mode = mode,
                questions = it,
                questionIndex = preferences[keys.index] ?: 0,
                correctCount = preferences[keys.correct] ?: 0,
                selectedAction = preferences[keys.selectedAction]
                    ?.let { runCatching { PokerAction.valueOf(it) }.getOrNull() },
            )
        }
    }

    private fun activeKeys(preferences: Preferences, mode: TrainingMode): SessionKeys {
        val dedicated = keysFor(mode)
        return if (preferences[dedicated.questions] != null) dedicated else LEGACY_KEYS
    }

    private fun legacyMode(preferences: Preferences): TrainingMode? =
        preferences[SESSION_MODE]
            ?.let { runCatching { TrainingMode.valueOf(it) }.getOrNull() }
            ?: if (preferences[LEGACY_SESSION_HANDS] != null || preferences[SESSION_QUESTIONS] != null) {
                TrainingMode.GUIDED
            } else {
                null
            }

    private fun clearLegacySession(preferences: androidx.datastore.preferences.core.MutablePreferences) {
        preferences.remove(SESSION_ID)
        preferences.remove(SESSION_MODE)
        preferences.remove(SESSION_QUESTIONS)
        preferences.remove(LEGACY_SESSION_HANDS)
        preferences.remove(SESSION_INDEX)
        preferences.remove(SESSION_CORRECT)
        preferences.remove(SESSION_SELECTED_ACTION)
    }

    private fun writeSession(
        preferences: androidx.datastore.preferences.core.MutablePreferences,
        session: StoredTrainingSession?,
    ) {
        if (session == null) return
        val keys = keysFor(session.mode)
        preferences[keys.id] = session.id
        preferences[keys.questions] = encodeQuestions(session.questions)
        preferences[keys.index] = session.questionIndex
        preferences[keys.correct] = session.correctCount
        session.selectedAction?.let { preferences[keys.selectedAction] = it.name }
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

        private val GUIDED_SESSION_ID = stringPreferencesKey("guided_session_id")
        private val GUIDED_SESSION_QUESTIONS = stringPreferencesKey("guided_session_questions")
        private val GUIDED_SESSION_INDEX = intPreferencesKey("guided_session_index")
        private val GUIDED_SESSION_CORRECT = intPreferencesKey("guided_session_correct")
        private val GUIDED_SESSION_SELECTED_ACTION = stringPreferencesKey("guided_session_selected_action")

        private val FREE_SESSION_ID = stringPreferencesKey("free_session_id")
        private val FREE_SESSION_QUESTIONS = stringPreferencesKey("free_session_questions")
        private val FREE_SESSION_INDEX = intPreferencesKey("free_session_index")
        private val FREE_SESSION_CORRECT = intPreferencesKey("free_session_correct")
        private val FREE_SESSION_SELECTED_ACTION = stringPreferencesKey("free_session_selected_action")

        private val LEGACY_KEYS = SessionKeys(
            id = SESSION_ID,
            questions = SESSION_QUESTIONS,
            index = SESSION_INDEX,
            correct = SESSION_CORRECT,
            selectedAction = SESSION_SELECTED_ACTION,
        )

        private val GUIDED_KEYS = SessionKeys(
            id = GUIDED_SESSION_ID,
            questions = GUIDED_SESSION_QUESTIONS,
            index = GUIDED_SESSION_INDEX,
            correct = GUIDED_SESSION_CORRECT,
            selectedAction = GUIDED_SESSION_SELECTED_ACTION,
        )

        private val FREE_KEYS = SessionKeys(
            id = FREE_SESSION_ID,
            questions = FREE_SESSION_QUESTIONS,
            index = FREE_SESSION_INDEX,
            correct = FREE_SESSION_CORRECT,
            selectedAction = FREE_SESSION_SELECTED_ACTION,
        )

        private fun keysFor(mode: TrainingMode): SessionKeys = when (mode) {
            TrainingMode.GUIDED -> GUIDED_KEYS
            TrainingMode.FREE -> FREE_KEYS
        }
    }
}

private data class SessionKeys(
    val id: Preferences.Key<String>,
    val questions: Preferences.Key<String>,
    val index: Preferences.Key<Int>,
    val correct: Preferences.Key<Int>,
    val selectedAction: Preferences.Key<String>,
)
