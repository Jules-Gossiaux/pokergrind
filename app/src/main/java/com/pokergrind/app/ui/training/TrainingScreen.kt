package com.pokergrind.app.ui.training

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokergrind.app.data.local.ProgressStore
import com.pokergrind.app.data.local.StoredTrainingSession
import com.pokergrind.app.domain.model.HandCategory
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.training.TrainingMode
import com.pokergrind.app.ui.range.RangeDialog
import com.pokergrind.app.ui.theme.Error
import com.pokergrind.app.ui.theme.Progress
import com.pokergrind.app.ui.theme.Success
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.SurfaceSoft
import com.pokergrind.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun TrainingScreen(
    range: RangeDefinition,
    session: StoredTrainingSession?,
    onAnswer: (PokerAction, Long) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit,
) {
    var showRange by remember { mutableStateOf(false) }
    val currentHand = session?.let { currentSession ->
        currentSession.currentQuestion
            ?.let { question ->
                range.entries.firstOrNull { it.hand.notation == question.handNotation }?.hand
            }
    }

    if (showRange) {
        RangeDialog(
            range = range,
            highlightedHand = currentHand,
            onDismiss = { showRange = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        when {
            session == null -> LoadingSession()
            session.isComplete -> SessionSummary(
                correctCount = session.correctCount,
                mode = session.mode,
                onRestart = onRestart,
                onBack = onBack,
            )

            currentHand != null -> QuestionContent(
                range = range,
                hand = currentHand,
                session = session,
                onAnswer = onAnswer,
                onNext = onNext,
                onShowRange = { showRange = true },
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun LoadingSession() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ColumnScope.QuestionContent(
    range: RangeDefinition,
    hand: HandCategory,
    session: StoredTrainingSession,
    onAnswer: (PokerAction, Long) -> Unit,
    onNext: () -> Unit,
    onShowRange: () -> Unit,
    onBack: () -> Unit,
) {
    val selectedAction = session.selectedAction
    val expectedAction = range.actionFor(hand)
    val isCorrect = selectedAction == expectedAction
    var elapsedSeconds by remember(hand.notation) { mutableIntStateOf(0) }

    LaunchedEffect(hand.notation, selectedAction) {
        while (selectedAction == null) {
            delay(1_000)
            elapsedSeconds++
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("Quitter")
        }
        Text(
            text = "${session.questionIndex + 1} / ${session.questions.size}",
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
        )
    }

    Spacer(Modifier.height(14.dp))
    LinearProgressIndicator(
        progress = { (session.questionIndex + 1f) / session.questions.size },
        modifier = Modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(RoundedCornerShape(100.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = SurfaceElevated,
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (session.mode == TrainingMode.GUIDED) "SESSION GUIDÉE" else "ENTRAÎNEMENT LIBRE",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.8.sp,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Tout le monde passe jusqu’au BTN",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${range.stackDepthBb} BB effectifs",
            color = TextSecondary,
        )

        Spacer(Modifier.height(42.dp))
        Surface(
            color = SurfaceSoft,
            shape = RoundedCornerShape(30.dp),
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 54.dp, vertical = 34.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = hand.notation,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.displayLarge,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "${elapsedSeconds}s · sans pénalité",
            color = TextSecondary,
        )
    }

    if (selectedAction == null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ActionButton(
                label = "Fold",
                modifier = Modifier.weight(1f),
                containerColor = SurfaceElevated,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = { onAnswer(PokerAction.FOLD, elapsedSeconds * 1_000L) },
            )
            ActionButton(
                label = "Open 2,5 BB",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = { onAnswer(PokerAction.OPEN, elapsedSeconds * 1_000L) },
            )
        }
    } else {
        Surface(
            color = if (isCorrect) Success.copy(alpha = 0.16f) else Error.copy(alpha = 0.16f),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    text = if (isCorrect) {
                        "+${ProgressStore.XP_PER_CORRECT_ANSWER} XP · Correct"
                    } else {
                        "Incorrect"
                    },
                    color = if (isCorrect) Success else Error,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = when (expectedAction) {
                        PokerAction.OPEN -> "La bonne action est Open 2,5 BB."
                        PokerAction.FOLD -> "La bonne action est Fold."
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onShowRange,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text("Voir la range")
                    }
                    Button(
                        onClick = onNext,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text("Continuer")
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    modifier: Modifier,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Text(label)
    }
}

@Composable
private fun SessionSummary(
    correctCount: Int,
    mode: TrainingMode,
    onRestart: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (mode == TrainingMode.GUIDED) {
                "SESSION GUIDÉE TERMINÉE"
            } else {
                "ENTRAÎNEMENT TERMINÉ"
            },
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.8.sp,
        )
        Spacer(Modifier.height(10.dp))
        Text("Bien joué.", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "$correctCount / 20",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displayLarge,
        )
        Text("bonnes réponses", color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${correctCount * ProgressStore.XP_PER_CORRECT_ANSWER} XP gagnés",
            color = Progress,
            fontWeight = FontWeight.SemiBold,
        )
        if (mode == TrainingMode.FREE) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Les erreurs ont été ajoutées aux priorités du guidé.",
                color = TextSecondary,
            )
        }
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
        ) {
            Text("Retour à l’accueil")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
        ) {
            Text("Nouvelle session")
        }
    }
}
