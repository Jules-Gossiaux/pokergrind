package com.pokergrind.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pokergrind.app.data.AnswerRepository
import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.data.local.PokerGrindDatabase
import com.pokergrind.app.data.local.ProgressStore
import com.pokergrind.app.data.local.StoredProgress
import com.pokergrind.app.data.local.StoredQuestion
import com.pokergrind.app.data.local.StoredTrainingSession
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.training.GuidedSessionPlanner
import com.pokergrind.app.domain.training.MasteryCalculator
import com.pokergrind.app.domain.training.SessionFactory
import com.pokergrind.app.domain.training.SpotMastery
import com.pokergrind.app.domain.training.TrainingMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PokerGrindUiState(
    val isLoading: Boolean = true,
    val xp: Int = 0,
    val streak: Int = 0,
    val freeAnswerCount: Int = 0,
    val session: StoredTrainingSession? = null,
    val btnMastery: SpotMastery = MasteryCalculator.empty,
    val unlockedSpotIds: Set<String> = emptySet(),
)

class PokerGrindViewModel(application: Application) : AndroidViewModel(application) {
    private val progressStore = ProgressStore(application)
    private val database = PokerGrindDatabase.getInstance(application)
    private val answerRepository = AnswerRepository(
        answerDao = database.answerDao(),
        reviewDao = database.reviewDao(),
        spotUnlockDao = database.spotUnlockDao(),
        progressStore = progressStore,
    )
    private val rangesById: Map<String, RangeDefinition> = listOf(BtnOpenRange.definition)
        .associateBy(RangeDefinition::id)
    private val btnRange = BtnOpenRange.definition
    private val recentBtnAnswers = answerRepository.observeRecentAnswers(
        btnRange.id,
        MasteryCalculator.WINDOW_SIZE,
    )

    val uiState: StateFlow<PokerGrindUiState> = combine(
        progressStore.progress,
        recentBtnAnswers,
        answerRepository.observeUnlockedSpotIds(),
        answerRepository.observeFreeAnswerCount(),
    ) { progress, recentAnswers, unlockedSpotIds, freeAnswerCount ->
        val mastery = MasteryCalculator.calculate(recentAnswers)
        if (mastery.isMastered && CO_SPOT_ID !in unlockedSpotIds) {
            viewModelScope.launch { answerRepository.ensureUnlocked(CO_SPOT_ID) }
        }
        progress.toUiState(
            mastery = mastery,
            unlockedSpotIds = unlockedSpotIds,
            freeAnswerCount = freeAnswerCount,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PokerGrindUiState(),
        )

    init {
        viewModelScope.launch {
            answerRepository.ensureUnlocked(btnRange.id)
        }
    }

    fun startGuidedSession() {
        if (hasActiveSession()) return
        viewModelScope.launch {
            val availableRanges = uiState.value.unlockedSpotIds
                .mapNotNull(rangesById::get)
                .ifEmpty { listOf(btnRange) }
            val reviewStates = answerRepository.reviewStates(availableRanges.map(RangeDefinition::id))
            val questions = GuidedSessionPlanner.plan(
                ranges = availableRanges,
                reviewStates = reviewStates,
                nowEpochMillis = System.currentTimeMillis(),
            ).map { (spotId, hand) ->
                StoredQuestion(spotId = spotId, handNotation = hand.notation)
            }
            progressStore.startSession(TrainingMode.GUIDED, questions)
        }
    }

    fun startFreeSession(spotId: String) {
        if (hasActiveSession()) return
        val range = rangesById[spotId] ?: return
        viewModelScope.launch {
            val questions = SessionFactory.createBalancedSession(range).map { hand ->
                StoredQuestion(spotId = range.id, handNotation = hand.notation)
            }
            progressStore.startSession(TrainingMode.FREE, questions)
        }
    }

    fun answer(action: PokerAction, responseTimeMillis: Long) {
        val session = uiState.value.session ?: return
        if (session.selectedAction != null || session.isComplete) return
        val question = session.currentQuestion ?: return
        val range = rangesById[question.spotId] ?: return
        val hand = range.entries.firstOrNull {
            it.hand.notation == question.handNotation
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
        viewModelScope.launch { progressStore.moveToNextQuestion() }
    }

    fun rangeForCurrentQuestion(): RangeDefinition {
        val spotId = uiState.value.session?.currentQuestion?.spotId
        return rangesById[spotId] ?: btnRange
    }

    private fun hasActiveSession(): Boolean =
        uiState.value.session?.let { !it.isComplete } == true

    private fun StoredProgress.toUiState(
        mastery: SpotMastery,
        unlockedSpotIds: Set<String>,
        freeAnswerCount: Int,
    ) = PokerGrindUiState(
        isLoading = false,
        xp = xp,
        streak = streak,
        freeAnswerCount = freeAnswerCount,
        session = session,
        btnMastery = mastery,
        unlockedSpotIds = unlockedSpotIds,
    )

    companion object {
        const val CO_SPOT_ID = "open_co_100bb_v1"
    }
}
