package com.pokergrind.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pokergrind.app.data.AnswerRepository
import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.data.CoOpenRange
import com.pokergrind.app.data.StatisticsRepository
import com.pokergrind.app.data.local.PokerGrindDatabase
import com.pokergrind.app.data.local.ProgressStore
import com.pokergrind.app.data.local.StoredProgress
import com.pokergrind.app.data.local.StoredQuestion
import com.pokergrind.app.data.local.StoredTrainingSession
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.statistics.StatisticsSnapshot
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
    val session: StoredTrainingSession? = null,
    val masteryBySpot: Map<String, SpotMastery> = emptyMap(),
    val unlockedSpotIds: Set<String> = emptySet(),
    val statistics: StatisticsSnapshot = StatisticsSnapshot(),
) {
    val btnMastery: SpotMastery
        get() = masteryBySpot[BtnOpenRange.definition.id] ?: MasteryCalculator.empty

    val coMastery: SpotMastery
        get() = masteryBySpot[CoOpenRange.definition.id] ?: MasteryCalculator.empty
}

class PokerGrindViewModel(application: Application) : AndroidViewModel(application) {
    private val progressStore = ProgressStore(application)
    private val database = PokerGrindDatabase.getInstance(application)
    private val answerRepository = AnswerRepository(
        answerDao = database.answerDao(),
        reviewDao = database.reviewDao(),
        spotUnlockDao = database.spotUnlockDao(),
        progressStore = progressStore,
    )
    private val statisticsRepository = StatisticsRepository(database.answerDao())
    private val ranges: List<RangeDefinition> = listOf(
        BtnOpenRange.definition,
        CoOpenRange.definition,
    )
    private val rangesById = ranges.associateBy(RangeDefinition::id)
    private val btnRange = BtnOpenRange.definition
    private val coRange = CoOpenRange.definition

    private val masteryFlow = combine(
        answerRepository.observeRecentAnswers(btnRange.id, MasteryCalculator.WINDOW_SIZE),
        answerRepository.observeRecentAnswers(coRange.id, MasteryCalculator.WINDOW_SIZE),
    ) { btnAnswers, coAnswers ->
        mapOf(
            btnRange.id to MasteryCalculator.calculate(btnAnswers),
            coRange.id to MasteryCalculator.calculate(coAnswers),
        )
    }

    val uiState: StateFlow<PokerGrindUiState> = combine(
        progressStore.progress,
        masteryFlow,
        answerRepository.observeUnlockedSpotIds(),
        statisticsRepository.statistics,
    ) { progress, masteryBySpot, unlockedSpotIds, statistics ->
        if (masteryBySpot.getValue(btnRange.id).isMastered && coRange.id !in unlockedSpotIds) {
            viewModelScope.launch { answerRepository.ensureUnlocked(coRange.id) }
        }
        if (masteryBySpot.getValue(coRange.id).isMastered && HJ_SPOT_ID !in unlockedSpotIds) {
            viewModelScope.launch { answerRepository.ensureUnlocked(HJ_SPOT_ID) }
        }
        progress.toUiState(
            masteryBySpot = masteryBySpot,
            unlockedSpotIds = unlockedSpotIds,
            statistics = statistics,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PokerGrindUiState(),
        )

    init {
        viewModelScope.launch { answerRepository.ensureUnlocked(btnRange.id) }
    }

    fun startGuidedSession() {
        if (hasActiveSession()) return
        viewModelScope.launch {
            val availableRanges = ranges.filter { it.id in uiState.value.unlockedSpotIds }
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
        if (spotId !in uiState.value.unlockedSpotIds) return
        viewModelScope.launch {
            progressStore.startSession(
                mode = TrainingMode.FREE,
                questions = SessionFactory.createBalancedSession(range).map { hand ->
                    StoredQuestion(spotId = range.id, handNotation = hand.notation)
                },
            )
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
        masteryBySpot: Map<String, SpotMastery>,
        unlockedSpotIds: Set<String>,
        statistics: StatisticsSnapshot,
    ) = PokerGrindUiState(
        isLoading = false,
        xp = xp,
        streak = streak,
        session = session,
        masteryBySpot = masteryBySpot,
        unlockedSpotIds = unlockedSpotIds,
        statistics = statistics,
    )

    companion object {
        const val HJ_SPOT_ID = "open_hj_100bb_v1"
    }
}
