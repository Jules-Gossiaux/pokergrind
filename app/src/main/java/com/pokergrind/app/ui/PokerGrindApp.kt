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
import com.pokergrind.app.ui.home.HomeScreen
import com.pokergrind.app.ui.training.TrainingScreen

private enum class Destination {
    HOME,
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
                range = BtnOpenRange.definition,
                xp = uiState.xp,
                streak = uiState.streak,
                activeSession = uiState.session?.takeUnless { it.isComplete },
                btnMastery = uiState.btnMastery,
                onStartTraining = {
                    viewModel.startOrResumeSession()
                    destination = Destination.TRAINING
                },
            )

            Destination.TRAINING -> TrainingScreen(
                range = BtnOpenRange.definition,
                session = uiState.session,
                onAnswer = viewModel::answer,
                onNext = viewModel::moveToNextQuestion,
                onRestart = viewModel::startOrResumeSession,
                onBack = { destination = Destination.HOME },
            )
        }
    }
}
