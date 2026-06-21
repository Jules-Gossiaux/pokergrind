package com.pokergrind.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.domain.training.TrainingMode
import com.pokergrind.app.ui.home.HomeScreen
import com.pokergrind.app.ui.statistics.StatisticsScreen
import com.pokergrind.app.ui.training.FreeSpotScreen
import com.pokergrind.app.ui.training.TrainingScreen

private enum class Destination {
    HOME,
    FREE_SPOT,
    STATISTICS,
    TRAINING,
}

@Composable
fun PokerGrindApp(viewModel: PokerGrindViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Surface
        }

        when (destination) {
            Destination.HOME -> HomeScreen(
                ranges = viewModel.ranges,
                xp = uiState.xp,
                streak = uiState.streak,
                activeSession = uiState.session?.takeUnless { it.isComplete },
                masteryBySpot = uiState.masteryBySpot,
                unlockedSpotIds = uiState.unlockedSpotIds,
                onStartTraining = {
                    viewModel.startGuidedSession()
                    destination = Destination.TRAINING
                },
                onOpenFreeTraining = {
                    val activeSession = uiState.session?.takeUnless { it.isComplete }
                    destination = if (activeSession?.mode == TrainingMode.FREE) {
                        Destination.TRAINING
                    } else {
                        Destination.FREE_SPOT
                    }
                },
                onOpenStatistics = { destination = Destination.STATISTICS },
            )

            Destination.FREE_SPOT -> FreeSpotScreen(
                ranges = viewModel.ranges,
                unlockedSpotIds = uiState.unlockedSpotIds,
                freeAnswerCount = uiState.statistics.freeSpotStats.sumOf { it.answerCount },
                onSelectSpot = { spotId ->
                    viewModel.startFreeSession(spotId)
                    destination = Destination.TRAINING
                },
                onBack = { destination = Destination.HOME },
            )

            Destination.STATISTICS -> StatisticsScreen(
                ranges = viewModel.ranges,
                statistics = uiState.statistics,
                masteryBySpot = uiState.masteryBySpot,
                unlockedSpotIds = uiState.unlockedSpotIds,
                onBack = { destination = Destination.HOME },
            )

            Destination.TRAINING -> TrainingScreen(
                range = viewModel.rangeForCurrentQuestion(),
                session = uiState.session,
                onAnswer = viewModel::answer,
                onNext = viewModel::moveToNextQuestion,
                onRestart = {
                    when (uiState.session?.mode) {
                        TrainingMode.FREE -> {
                            val spotId = uiState.session?.questions?.firstOrNull()?.spotId
                                ?: BtnOpenRange.definition.id
                            viewModel.startFreeSession(spotId)
                        }
                        else -> viewModel.startGuidedSession()
                    }
                },
                onBack = { destination = Destination.HOME },
            )
        }
    }
}
