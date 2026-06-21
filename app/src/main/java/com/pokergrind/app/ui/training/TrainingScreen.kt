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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pokergrind.app.domain.model.HandCategory
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.ui.range.RangeDialog
import com.pokergrind.app.ui.theme.Error
import com.pokergrind.app.ui.theme.Success
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private const val SESSION_SIZE = 20

@Composable
fun TrainingScreen(
    range: RangeDefinition,
    onBack: () -> Unit,
) {
    var sessionGeneration by remember { mutableIntStateOf(0) }
    val questions = remember(range.id, sessionGeneration) { createBalancedSession(range) }
    var questionIndex by remember { mutableIntStateOf(0) }
    var selectedAction by remember { mutableStateOf<PokerAction?>(null) }
    var correctCount by remember { mutableIntStateOf(0) }
    var showRange by remember { mutableStateOf(false) }

    val isFinished = questionIndex >= questions.size
    val currentHand = questions.getOrNull(questionIndex)

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
            .padding(20.dp),
    ) {
        if (isFinished) {
            SessionSummary(
                correctCount = correctCount,
                onRestart = {
                    sessionGeneration++
                    questionIndex = 0
                    selectedAction = null
                    correctCount = 0
                },
                onBack = onBack,
            )
        } else if (currentHand != null) {
            QuestionContent(
                range = range,
                hand = currentHand,
                questionIndex = questionIndex,
                selectedAction = selectedAction,
                onAnswer = { action ->
                    if (selectedAction == null) {
                        selectedAction = action
                        if (action == range.actionFor(currentHand)) correctCount++
                    }
                },
                onNext = {
                    questionIndex++
                    selectedAction = null
                },
                onShowRange = { showRange = true },
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun ColumnScope.QuestionContent(
    range: RangeDefinition,
    hand: HandCategory,
    questionIndex: Int,
    selectedAction: PokerAction?,
    onAnswer: (PokerAction) -> Unit,
    onNext: () -> Unit,
    onShowRange: () -> Unit,
    onBack: () -> Unit,
) {
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
        OutlinedButton(onClick = onBack) {
            Text("Quitter")
        }
        Text(
            text = "${questionIndex + 1} / $SESSION_SIZE",
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
        )
    }

    Spacer(Modifier.height(16.dp))
    LinearProgressIndicator(
        progress = { (questionIndex + 1f) / SESSION_SIZE },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
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
            text = "Tout le monde passe jusqu’au BTN",
            color = TextSecondary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tu es au ${range.position} · ${range.stackDepthBb} BB",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "Open à ${range.sizingBb} BB ou Fold ?",
            color = TextSecondary,
        )

        Spacer(Modifier.height(52.dp))
        Box(
            modifier = Modifier
                .background(SurfaceElevated, RoundedCornerShape(32.dp))
                .padding(horizontal = 52.dp, vertical = 36.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = hand.notation,
                style = MaterialTheme.typography.displayLarge,
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "${elapsedSeconds}s · temps non pénalisé",
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
                onClick = { onAnswer(PokerAction.FOLD) },
            )
            ActionButton(
                label = "Open",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = { onAnswer(PokerAction.OPEN) },
            )
        }
    } else {
        Surface(
            color = if (isCorrect) Success.copy(alpha = 0.18f) else Error.copy(alpha = 0.18f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = if (isCorrect) "Correct" else "Incorrect",
                    color = if (isCorrect) Success else Error,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "La bonne action est ${expectedAction.label}.",
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onShowRange,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Voir la range")
                    }
                    Button(
                        onClick = onNext,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Continuer")
                    }
                }
            }
        }
    }
}

private fun createBalancedSession(range: RangeDefinition): List<HandCategory> {
    val byAction = range.entries.groupBy { it.action }
    val perAction = SESSION_SIZE / 2

    return buildList {
        addAll(byAction.getValue(PokerAction.OPEN).shuffled().take(perAction))
        addAll(byAction.getValue(PokerAction.FOLD).shuffled().take(perAction))
    }
        .shuffled()
        .map { it.hand }
}

@Composable
private fun ActionButton(
    label: String,
    modifier: Modifier,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(22.dp),
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
    onRestart: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Session terminée", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "$correctCount / $SESSION_SIZE",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displayLarge,
        )
        Text("bonnes réponses", color = TextSecondary)
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
        ) {
            Text("Recommencer")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text("Retour à l’accueil")
        }
    }
}
