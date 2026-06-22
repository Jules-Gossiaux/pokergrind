package com.pokergrind.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pokergrind.app.data.AnswerRepository
import com.pokergrind.app.data.BackupManager
import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.data.OpenRanges
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
import com.pokergrind.app.domain.training.ReviewState
import com.pokergrind.app.domain.training.SessionFactory
import com.pokergrind.app.domain.training.SpotMastery
import com.pokergrind.app.domain.training.TrainingMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PokerGrindUiState(
    val isLoading: Boolean = true,
    val xp: Int = 0,
    val streak: Int = 0,
    val guidedSession: StoredTrainingSession? = null,
    val freeSession: StoredTrainingSession? = null,
    val masteryBySpot: Map<String, SpotMastery> = emptyMap(),
    val unlockedSpotIds: Set<String> = emptySet(),
    val statistics: StatisticsSnapshot = StatisticsSnapshot(),
    val reviewStates: Map<Pair<String, String>, ReviewState> = emptyMap(),
) {
    val btnMastery: SpotMastery
        get() = masteryBySpot[BtnOpenRange.definition.id] ?: MasteryCalculator.empty

    fun sessionFor(mode: TrainingMode): StoredTrainingSession? = when (mode) {
        TrainingMode.GUIDED -> guidedSession
        TrainingMode.FREE -> freeSession
    }
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
    private val backupManager = BackupManager(application, database, progressStore)
    val ranges: List<RangeDefinition> = OpenRanges.all
    private val rangesById = ranges.associateBy(RangeDefinition::id)
    private val btnRange = BtnOpenRange.definition

    private val masteryFlow = combine(
        ranges.map { range ->
            answerRepository
                .observeRecentAnswers(range.id, MasteryCalculator.WINDOW_SIZE)
                .map { answers -> range.id to MasteryCalculator.calculate(answers) }
        },
    ) { masteryPairs ->
        masteryPairs.toMap()
    }

    val uiState: StateFlow<PokerGrindUiState> = combine(
        progressStore.progress,
        masteryFlow,
        answerRepository.observeUnlockedSpotIds(),
        statisticsRepository.statistics,
        answerRepository.observeReviewStates(),
    ) { progress, masteryBySpot, unlockedSpotIds, statistics, reviewStates ->
        ranges.zipWithNext().forEach { (current, next) ->
            if (masteryBySpot.getValue(current.id).isMastered && next.id !in unlockedSpotIds) {
                viewModelScope.launch { answerRepository.ensureUnlocked(next.id) }
            }
        }
        progress.toUiState(
            masteryBySpot = masteryBySpot,
            unlockedSpotIds = unlockedSpotIds,
            statistics = statistics,
            reviewStates = reviewStates,
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
        if (uiState.value.guidedSession?.isComplete == false) return
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
        if (uiState.value.freeSession?.isComplete == false) return
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

    fun answer(mode: TrainingMode, action: PokerAction, responseTimeMillis: Long) {
        val session = uiState.value.sessionFor(mode) ?: return
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

    fun moveToNextQuestion(mode: TrainingMode) {
        val session = uiState.value.sessionFor(mode) ?: return
        if (session.selectedAction == null || session.isComplete) return
        viewModelScope.launch { progressStore.moveToNextQuestion(mode) }
    }

    fun rangeForCurrentQuestion(mode: TrainingMode): RangeDefinition {
        val spotId = uiState.value.sessionFor(mode)?.currentQuestion?.spotId
        return rangesById[spotId] ?: btnRange
    }

    fun exportBackup(uri: Uri, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(runCatching { backupManager.exportTo(uri) })
        }
    }

    fun importBackup(uri: Uri, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = runCatching {
                backupManager.importFrom(uri)
                answerRepository.ensureUnlocked(btnRange.id)
            }
            onResult(result)
        }
    }

    private fun StoredProgress.toUiState(
        masteryBySpot: Map<String, SpotMastery>,
        unlockedSpotIds: Set<String>,
        statistics: StatisticsSnapshot,
        reviewStates: Map<Pair<String, String>, ReviewState>,
    ) = PokerGrindUiState(
        isLoading = false,
        xp = xp,
        streak = streak,
        guidedSession = guidedSession,
        freeSession = freeSession,
        masteryBySpot = masteryBySpot,
        unlockedSpotIds = unlockedSpotIds,
        statistics = statistics,
        reviewStates = reviewStates,
    )
}
