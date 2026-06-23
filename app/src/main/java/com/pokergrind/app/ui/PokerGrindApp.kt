package com.pokergrind.app.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.domain.training.TrainingMode
import com.pokergrind.app.ui.home.FoundationsScreen
import com.pokergrind.app.ui.statistics.StatisticsScreen
import com.pokergrind.app.ui.training.FreeSpotScreen
import com.pokergrind.app.ui.training.TrainingScreen

private enum class Destination {
    FOUNDATIONS,
    FREE_SPOT,
    STATISTICS,
    TRAINING,
}

@Composable
fun PokerGrindApp(viewModel: PokerGrindViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var destination by rememberSaveable { mutableStateOf(Destination.FOUNDATIONS) }
    var trainingMode by rememberSaveable { mutableStateOf(TrainingMode.GUIDED) }
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        viewModel.exportBackup(uri) { result ->
            Toast.makeText(
                context,
                if (result.isSuccess) "Sauvegarde créée" else "Échec de la sauvegarde",
                Toast.LENGTH_LONG,
            ).show()
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        viewModel.importBackup(uri) { result ->
            Toast.makeText(
                context,
                if (result.isSuccess) "Progression restaurée" else "Fichier de sauvegarde invalide",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    BackHandler(enabled = destination != Destination.FOUNDATIONS) {
        destination = when (destination) {
            Destination.FREE_SPOT,
            Destination.STATISTICS,
            Destination.TRAINING,
            -> Destination.FOUNDATIONS
            Destination.FOUNDATIONS -> Destination.FOUNDATIONS
        }
    }

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
            Destination.FOUNDATIONS -> FoundationsScreen(
                openRanges = viewModel.openRanges,
                bbDefenseRanges = viewModel.bbDefenseRanges,
                xp = uiState.xp,
                streak = uiState.streak,
                masteredOpenSpots = viewModel.openRanges.count { range ->
                    uiState.masteryBySpot[range.id]?.isMastered == true
                },
                unlockedOpenSpots = uiState.unlockedSpotIds.count { id ->
                    viewModel.openRanges.any { it.id == id }
                },
                masteredBbDefenseSpots = viewModel.bbDefenseRanges.count { range ->
                    uiState.masteryBySpot[range.id]?.isMastered == true
                },
                unlockedBbDefenseSpots = uiState.unlockedSpotIds.count { id ->
                    viewModel.bbDefenseRanges.any { it.id == id }
                },
                guidedSession = uiState.guidedSession?.takeUnless { it.isComplete },
                freeSession = uiState.freeSession?.takeUnless { it.isComplete },
                masteryBySpot = uiState.masteryBySpot,
                unlockedSpotIds = uiState.unlockedSpotIds,
                onStartTraining = {
                    viewModel.startGuidedSession()
                    trainingMode = TrainingMode.GUIDED
                    destination = Destination.TRAINING
                },
                onOpenFreeTraining = {
                    destination = if (uiState.freeSession?.isComplete == false) {
                        trainingMode = TrainingMode.FREE
                        Destination.TRAINING
                    } else {
                        Destination.FREE_SPOT
                    }
                },
                onStartFreeSpot = { spotId ->
                    viewModel.startFreeSession(spotId, replaceExisting = true)
                    trainingMode = TrainingMode.FREE
                    destination = Destination.TRAINING
                },
                onOpenStatistics = { destination = Destination.STATISTICS },
                onExportBackup = {
                    exportLauncher.launch("pokergrind-backup-${java.time.LocalDate.now()}.json")
                },
                onImportBackup = { importLauncher.launch(arrayOf("application/json", "text/plain")) },
            )

            Destination.FREE_SPOT -> FreeSpotScreen(
                ranges = viewModel.ranges,
                unlockedSpotIds = uiState.unlockedSpotIds,
                freeAnswerCount = uiState.statistics.freeSpotStats.sumOf { it.answerCount },
                onSelectSpot = { spotId ->
                    viewModel.startFreeSession(spotId)
                    trainingMode = TrainingMode.FREE
                    destination = Destination.TRAINING
                },
                onBack = { destination = Destination.FOUNDATIONS },
            )

            Destination.STATISTICS -> StatisticsScreen(
                ranges = viewModel.ranges,
                statistics = uiState.statistics,
                masteryBySpot = uiState.masteryBySpot,
                reviewStates = uiState.reviewStates,
                unlockedSpotIds = uiState.unlockedSpotIds,
                onBack = { destination = Destination.FOUNDATIONS },
            )

            Destination.TRAINING -> TrainingScreen(
                range = viewModel.rangeForCurrentQuestion(trainingMode),
                session = uiState.sessionFor(trainingMode),
                onAnswer = { action, responseTime ->
                    viewModel.answer(trainingMode, action, responseTime)
                },
                onNext = { viewModel.moveToNextQuestion(trainingMode) },
                onRestart = {
                    when (trainingMode) {
                        TrainingMode.FREE -> {
                            val spotId = uiState.freeSession?.questions?.firstOrNull()?.spotId
                                ?: BtnOpenRange.definition.id
                            viewModel.startFreeSession(spotId)
                        }
                        else -> viewModel.startGuidedSession()
                    }
                },
                onBack = { destination = Destination.FOUNDATIONS },
            )
        }
    }
}
