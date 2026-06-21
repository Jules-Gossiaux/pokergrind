package com.pokergrind.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.training.ProgressionRules
import java.io.IOException
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.progressDataStore by preferencesDataStore(name = "pokergrind_progress")

data class StoredTrainingSession(
    val hands: List<String>,
    val questionIndex: Int,
    val correctCount: Int,
    val selectedAction: PokerAction?,
) {
    val isComplete: Boolean
        get() = hands.isNotEmpty() && questionIndex >= hands.size
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

    suspend fun startSession(hands: List<String>) {
        context.progressDataStore.edit { preferences ->
            preferences[SESSION_HANDS] = hands.joinToString(",")
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
            val hands = decodeHands(preferences[SESSION_HANDS])
            val nextIndex = (preferences[SESSION_INDEX] ?: 0) + 1
            preferences[SESSION_INDEX] = nextIndex
            preferences.remove(SESSION_SELECTED_ACTION)

            if (hands.isNotEmpty() && nextIndex >= hands.size) {
                updateStreak(preferences, LocalDate.now())
            }
        }
    }

    private fun toStoredProgress(preferences: Preferences): StoredProgress {
        val hands = decodeHands(preferences[SESSION_HANDS])
        val session = hands.takeIf { it.isNotEmpty() }?.let {
            StoredTrainingSession(
                hands = it,
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

    private fun decodeHands(value: String?): List<String> =
        value
            ?.split(",")
            ?.filter(String::isNotBlank)
            .orEmpty()

    companion object {
        const val XP_PER_CORRECT_ANSWER = 10

        private val XP = intPreferencesKey("xp")
        private val STREAK = intPreferencesKey("streak")
        private val LAST_COMPLETED_DATE = stringPreferencesKey("last_completed_date")
        private val SESSION_HANDS = stringPreferencesKey("session_hands")
        private val SESSION_INDEX = intPreferencesKey("session_index")
        private val SESSION_CORRECT = intPreferencesKey("session_correct")
        private val SESSION_SELECTED_ACTION = stringPreferencesKey("session_selected_action")
    }
}
