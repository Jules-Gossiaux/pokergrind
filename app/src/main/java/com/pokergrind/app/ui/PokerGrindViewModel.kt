package com.pokergrind.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.data.AnswerRepository
import com.pokergrind.app.data.local.PokerGrindDatabase
import com.pokergrind.app.data.local.ProgressStore
import com.pokergrind.app.data.local.StoredProgress
import com.pokergrind.app.data.local.StoredTrainingSession
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.training.SessionFactory
import com.pokergrind.app.domain.training.MasteryCalculator
import com.pokergrind.app.domain.training.SpotMastery
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PokerGrindUiState(
    val isLoading: Boolean = true,
    val xp: Int = 0,
    val streak: Int = 0,
    val session: StoredTrainingSession? = null,
    val btnMastery: SpotMastery = MasteryCalculator.empty,
)

class PokerGrindViewModel(application: Application) : AndroidViewModel(application) {
    private val progressStore = ProgressStore(application)
    private val range = BtnOpenRange.definition
    private val answerRepository = AnswerRepository(
        answerDao = PokerGrindDatabase.getInstance(application).answerDao(),
        progressStore = progressStore,
    )

    val uiState: StateFlow<PokerGrindUiState> = combine(
        progressStore.progress,
        answerRepository.observeRecentAnswers(range.id, MasteryCalculator.WINDOW_SIZE),
    ) { progress, recentAnswers ->
        progress.toUiState(MasteryCalculator.calculate(recentAnswers))
    }
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

    fun answer(action: PokerAction, responseTimeMillis: Long) {
        val session = uiState.value.session ?: return
        if (session.selectedAction != null || session.isComplete) return
        val hand = range.entries.firstOrNull {
            it.hand.notation == session.hands.getOrNull(session.questionIndex)
        }?.hand ?: return

        viewModelScope.launch {
            answerRepository.recordAnswer(
                session = session,
                spotId = range.id,
                handNotation = hand.notation,
                expectedAction = range.actionFor(hand),
                chosenAction = action,
                responseTimeMillis = responseTimeMillis,
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

    private fun StoredProgress.toUiState(mastery: SpotMastery) = PokerGrindUiState(
        isLoading = false,
        xp = xp,
        streak = streak,
        session = session,
        btnMastery = mastery,
    )
}
