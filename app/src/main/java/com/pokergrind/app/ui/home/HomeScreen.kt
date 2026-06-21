package com.pokergrind.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokergrind.app.data.local.StoredTrainingSession
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.training.MasteryCalculator
import com.pokergrind.app.domain.training.SpotMastery
import com.pokergrind.app.domain.training.TrainingMode
import com.pokergrind.app.ui.theme.Progress
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.SurfaceSoft
import com.pokergrind.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    ranges: List<RangeDefinition>,
    xp: Int,
    streak: Int,
    activeSession: StoredTrainingSession?,
    masteryBySpot: Map<String, SpotMastery>,
    unlockedSpotIds: Set<String>,
    onStartTraining: () -> Unit,
    onOpenFreeTraining: () -> Unit,
    onOpenStatistics: () -> Unit,
) {
    val firstRange = ranges.first()
    val firstMastery = masteryBySpot[firstRange.id] ?: MasteryCalculator.empty
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
            .padding(top = 34.dp, bottom = 20.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "POKERGRIND",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.2.sp,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "Construis tes réflexes.",
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = "Une décision à la fois, jusqu’à ce qu’elle devienne automatique.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = xp.toString(),
                    label = "XP gagnés",
                    accent = Progress,
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = streak.toString(),
                    label = if (streak > 1) "jours de série" else "jour de série",
                    accent = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(30.dp))

            Text(
                text = "FONDATIONS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Ranges d’open",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = if (firstMastery.isMastered) {
                    "Open BTN maîtrisé. Le prochain spot est débloqué."
                } else {
                    "Le prochain spot se débloque à 90 % de réussite sur tes 30 dernières réponses."
                },
                color = TextSecondary,
            )

            Spacer(Modifier.height(18.dp))

            ranges.forEachIndexed { index, range ->
                val unlocked = range.id in unlockedSpotIds
                val mastery = masteryBySpot[range.id] ?: MasteryCalculator.empty
                val sessionTouchesRange = activeSession?.currentQuestion?.spotId == range.id
                PathStep(
                    number = index + 1,
                    title = range.title,
                    subtitle = if (sessionTouchesRange) {
                        val label = if (activeSession?.mode == TrainingMode.GUIDED) "Guidé" else "Libre"
                        "$label en cours · ${(activeSession?.questionIndex?.plus(1) ?: 1)}/20"
                    } else if (unlocked) {
                        "${range.stackDepthBb} BB · Open 2,5 BB"
                    } else {
                        "À débloquer"
                    },
                    isActive = index == 0,
                    isUnlocked = unlocked,
                    unlockHint = if (unlocked) masteryLabel(mastery) else null,
                    connectorUnlocked = ranges.getOrNull(index + 1)?.id in unlockedSpotIds,
                    showConnector = index < ranges.lastIndex,
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        Column {
            Button(
                onClick = onStartTraining,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = activeSession?.mode != TrainingMode.FREE,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    if (activeSession?.mode == TrainingMode.GUIDED) {
                        "Reprendre le guidé · ${activeSession.questionIndex + 1}/20"
                    } else {
                        "Session guidée · 20 questions"
                    },
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onOpenFreeTraining,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(if (activeSession?.mode == TrainingMode.FREE) "Reprendre libre" else "Mode libre")
                }
                OutlinedButton(
                    onClick = onOpenStatistics,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Statistiques")
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    value: String,
    label: String,
    accent: Color,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Text(value, color = accent, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = label,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PathStep(
    number: Int,
    title: String,
    subtitle: String,
    isActive: Boolean = false,
    isUnlocked: Boolean = false,
    unlockHint: String? = null,
    connectorUnlocked: Boolean = false,
    showConnector: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (isActive || isUnlocked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            SurfaceElevated
                        },
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = number.toString(),
                    color = if (isActive || isUnlocked) MaterialTheme.colorScheme.onPrimary else TextSecondary,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 28.dp)
                        .background(
                            if (connectorUnlocked) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                            } else {
                                SurfaceElevated
                            },
                        ),
                )
            }
        }

        Card(
            modifier = Modifier
                .padding(start = 12.dp, bottom = 10.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isActive || isUnlocked) SurfaceElevated else Color.Transparent,
            ),
            shape = RoundedCornerShape(18.dp),
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 11.dp)) {
                Text(
                    text = title,
                    color = if (isActive || isUnlocked) MaterialTheme.colorScheme.onSurface else TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                )
                Text(
                    text = subtitle,
                    color = if (isActive || isUnlocked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        TextSecondary.copy(alpha = 0.65f)
                    },
                    fontSize = 14.sp,
                )
                if (unlockHint != null) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = unlockHint,
                        color = TextSecondary,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

private fun masteryLabel(mastery: SpotMastery): String = when {
    mastery.isMastered -> "Maîtrisé · ${mastery.correctCount}/30 · ${mastery.successRatePercent} %"
    mastery.answerCount < MasteryCalculator.WINDOW_SIZE ->
        "Progression : ${mastery.answerCount}/30 réponses · encore ${mastery.answersRemaining}"
    else ->
        "${mastery.correctCount}/30 correctes · ${mastery.successRatePercent} % · diversité " +
            "${mastery.distinctOpenHands}/8 Open, ${mastery.distinctFoldHands}/8 Fold"
}
