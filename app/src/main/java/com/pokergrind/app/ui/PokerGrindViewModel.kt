package com.pokergrind.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.data.local.ProgressStore
import com.pokergrind.app.data.local.StoredProgress
import com.pokergrind.app.data.local.StoredTrainingSession
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.training.SessionFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PokerGrindUiState(
    val isLoading: Boolean = true,
    val xp: Int = 0,
    val streak: Int = 0,
    val session: StoredTrainingSession? = null,
)

class PokerGrindViewModel(application: Application) : AndroidViewModel(application) {
    private val progressStore = ProgressStore(application)
    private val range = BtnOpenRange.definition

    val uiState: StateFlow<PokerGrindUiState> = progressStore.progress
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PokerGrindUiState(),
        )

    fun startOrResumeSession() {
        val session = uiState.value.session
        if (session != null && !session.isComplete) return

        viewModelScope.launch {
            progressStore.startSession(
                SessionFactory.createBalancedSession(range).map { it.notation },
            )
        }
    }

    fun answer(action: PokerAction) {
        val session = uiState.value.session ?: return
        if (session.selectedAction != null || session.isComplete) return
        val hand = range.entries.firstOrNull {
            it.hand.notation == session.hands.getOrNull(session.questionIndex)
        }?.hand ?: return

        viewModelScope.launch {
            progressStore.answer(
                action = action,
                isCorrect = action == range.actionFor(hand),
            )
        }
    }

    fun moveToNextQuestion() {
        val session = uiState.value.session ?: return
        if (session.selectedAction == null || session.isComplete) return

        viewModelScope.launch {
            progressStore.moveToNextQuestion()
        }
    }

    private fun StoredProgress.toUiState() = PokerGrindUiState(
        isLoading = false,
        xp = xp,
        streak = streak,
        session = session,
    )
}
