package com.pokergrind.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.pokergrind.app.ui.range.RangeDialog
import com.pokergrind.app.ui.theme.Progress
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.SurfaceSoft
import com.pokergrind.app.ui.theme.TextSecondary

@Composable
fun FoundationsScreen(
    openRanges: List<RangeDefinition>,
    bbDefenseRanges: List<RangeDefinition>,
    xp: Int,
    streak: Int,
    masteredOpenSpots: Int,
    unlockedOpenSpots: Int,
    masteredBbDefenseSpots: Int,
    unlockedBbDefenseSpots: Int,
    guidedSession: StoredTrainingSession?,
    freeSession: StoredTrainingSession?,
    masteryBySpot: Map<String, SpotMastery>,
    unlockedSpotIds: Set<String>,
    onStartTraining: () -> Unit,
    onOpenFreeTraining: () -> Unit,
    onOpenStatistics: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
) {
    var confirmImport by remember { mutableStateOf(false) }
    val shouldStartOnBbDefense = unlockedBbDefenseSpots > 0
    var opensExpanded by rememberSaveable { mutableStateOf(!shouldStartOnBbDefense) }
    var bbDefenseExpanded by rememberSaveable { mutableStateOf(shouldStartOnBbDefense) }
    var displayedRange by remember { mutableStateOf<RangeDefinition?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(shouldStartOnBbDefense, unlockedOpenSpots) {
        val targetScroll = when {
            shouldStartOnBbDefense -> 520
            unlockedOpenSpots >= 4 -> 520
            unlockedOpenSpots >= 2 -> 280
            else -> 0
        }
        scrollState.scrollTo(targetScroll)
    }

    displayedRange?.let { range ->
        RangeDialog(
            range = range,
            highlightedHand = null,
            onDismiss = { displayedRange = null },
        )
    }

    if (confirmImport) {
        AlertDialog(
            onDismissRequest = { confirmImport = false },
            title = { Text("Restaurer une sauvegarde ?") },
            text = {
                Text("Les données actuelles seront remplacées par celles du fichier sélectionné.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmImport = false
                        onImportBackup()
                    },
                ) {
                    Text("Choisir le fichier")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmImport = false }) {
                    Text("Annuler")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
            .padding(top = 34.dp, bottom = 20.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
        ) {
            Text(
                "POKERGRIND",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.2.sp,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Fondations",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
            )
            Text(
                "Construis tes réflexes préflop, chapitre par chapitre.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FoundationStat(
                    modifier = Modifier.weight(1f),
                    value = xp.toString(),
                    label = "XP gagnés",
                    accent = Progress,
                )
                FoundationStat(
                    modifier = Modifier.weight(1f),
                    value = streak.toString(),
                    label = if (streak > 1) "jours de série" else "jour de série",
                    accent = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(30.dp))
            Text(
                "FONDATIONS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(10.dp))

            ChapterCard(
                title = "Opens",
                subtitle = "$unlockedOpenSpots/${openRanges.size} spots débloqués · $masteredOpenSpots maîtrisés",
                detail = when {
                    guidedSession != null && freeSession != null -> "Sessions guidée et libre en cours"
                    guidedSession != null -> "Session guidée en cours"
                    freeSession != null -> "Session libre en cours"
                    else -> "BTN, CO, HJ, UTG et SB"
                },
                enabled = true,
                expanded = opensExpanded,
                onClick = {
                    opensExpanded = !opensExpanded
                    if (opensExpanded) bbDefenseExpanded = false
                },
            )

            if (opensExpanded) {
                Spacer(Modifier.height(16.dp))
                OpensPath(
                    ranges = openRanges,
                    guidedSession = guidedSession,
                    freeSession = freeSession,
                    masteryBySpot = masteryBySpot,
                    unlockedSpotIds = unlockedSpotIds,
                    onViewRange = { displayedRange = it },
                )
                Spacer(Modifier.height(16.dp))
            } else {
                Spacer(Modifier.height(12.dp))
            }

            ChapterCard(
                title = "Défenses BB",
                subtitle = if (unlockedBbDefenseSpots > 0) {
                    "$unlockedBbDefenseSpots/${bbDefenseRanges.size} spots débloqués · $masteredBbDefenseSpots maîtrisés"
                } else {
                    "${bbDefenseRanges.size} spot prêt"
                },
                detail = if (unlockedBbDefenseSpots > 0) {
                    "Call, 3-bet ou Fold face aux opens adverses"
                } else {
                    "Débloqué après les ranges d’open"
                },
                enabled = unlockedBbDefenseSpots > 0,
                expanded = bbDefenseExpanded,
                onClick = {
                    bbDefenseExpanded = !bbDefenseExpanded
                    if (bbDefenseExpanded) opensExpanded = false
                },
            )
            if (bbDefenseExpanded && unlockedBbDefenseSpots > 0) {
                Spacer(Modifier.height(16.dp))
                TrainingPath(
                    ranges = bbDefenseRanges,
                    guidedSession = guidedSession,
                    freeSession = freeSession,
                    masteryBySpot = masteryBySpot,
                    unlockedSpotIds = unlockedSpotIds,
                    onViewRange = { displayedRange = it },
                )
            }
            Spacer(Modifier.height(12.dp))
            ChapterCard(
                title = "3-bets",
                subtitle = "À venir",
                detail = "Construire puis défendre les ranges de 3-bet",
                enabled = false,
                expanded = false,
            )

            Spacer(Modifier.height(30.dp))
            Text("Tes données", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Crée un fichier pour protéger ta progression ou la transférer vers un autre téléphone.",
                color = TextSecondary,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onExportBackup,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Sauvegarder")
                }
                OutlinedButton(
                    onClick = { confirmImport = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Restaurer")
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        if (opensExpanded || bbDefenseExpanded) {
            FoundationActions(
                guidedSession = guidedSession,
                freeSession = freeSession,
                onStartTraining = onStartTraining,
                onOpenFreeTraining = onOpenFreeTraining,
                onOpenStatistics = onOpenStatistics,
            )
        }
    }
}

@Composable
private fun ChapterCard(
    title: String,
    subtitle: String,
    detail: String,
    enabled: Boolean,
    expanded: Boolean,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) SurfaceElevated else SurfaceSoft,
            disabledContainerColor = SurfaceSoft,
        ),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else TextSecondary,
                )
                if (!enabled) {
                    Text(
                        "Verrouillé",
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.height(7.dp))
            Text(subtitle, color = if (enabled) MaterialTheme.colorScheme.primary else TextSecondary)
            Text(detail, color = TextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun OpensPath(
    ranges: List<RangeDefinition>,
    guidedSession: StoredTrainingSession?,
    freeSession: StoredTrainingSession?,
    masteryBySpot: Map<String, SpotMastery>,
    unlockedSpotIds: Set<String>,
    onViewRange: (RangeDefinition) -> Unit,
) = TrainingPath(
    ranges = ranges,
    guidedSession = guidedSession,
    freeSession = freeSession,
    masteryBySpot = masteryBySpot,
    unlockedSpotIds = unlockedSpotIds,
    onViewRange = onViewRange,
)

@Composable
private fun TrainingPath(
    ranges: List<RangeDefinition>,
    guidedSession: StoredTrainingSession?,
    freeSession: StoredTrainingSession?,
    masteryBySpot: Map<String, SpotMastery>,
    unlockedSpotIds: Set<String>,
    onViewRange: (RangeDefinition) -> Unit,
) {
    val activeRangeId = ranges.lastOrNull { range -> range.id in unlockedSpotIds }?.id
        ?: ranges.firstOrNull()?.id

    ranges.forEachIndexed { index, range ->
        val unlocked = range.id in unlockedSpotIds
        val mastery = masteryBySpot[range.id] ?: MasteryCalculator.empty
        val guidedTouchesRange = guidedSession?.currentQuestion?.spotId == range.id
        val freeTouchesRange = freeSession?.currentQuestion?.spotId == range.id
        PathStep(
            number = index + 1,
            title = range.title,
            subtitle = when {
                guidedTouchesRange -> "Guidé en cours · ${(guidedSession?.questionIndex?.plus(1) ?: 1)}/20"
                freeTouchesRange -> "Libre en cours · ${(freeSession?.questionIndex?.plus(1) ?: 1)}/20"
                unlocked -> range.shortDescription()
                else -> "À débloquer"
            },
            isActive = range.id == activeRangeId,
            isUnlocked = unlocked,
            unlockHint = if (unlocked) masteryLabel(mastery) else null,
            connectorUnlocked = ranges.getOrNull(index + 1)?.id in unlockedSpotIds,
            showConnector = index < ranges.lastIndex,
            onViewRange = if (unlocked) {
                { onViewRange(range) }
            } else {
                null
            },
        )
    }
}

@Composable
private fun FoundationActions(
    guidedSession: StoredTrainingSession?,
    freeSession: StoredTrainingSession?,
    onStartTraining: () -> Unit,
    onOpenFreeTraining: () -> Unit,
    onOpenStatistics: () -> Unit,
) {
    Column {
        Button(
            onClick = onStartTraining,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                if (guidedSession != null) {
                    "Reprendre le guidé · ${guidedSession.questionIndex + 1}/20"
                } else {
                    "Session guidée · 20 questions"
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                Text(
                    if (freeSession != null) {
                        "Mode libre"
                    } else {
                        "Mode libre"
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            OutlinedButton(
                onClick = onOpenStatistics,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("Statistiques", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun FoundationStat(
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
    onViewRange: (() -> Unit)? = null,
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
                    color = if (isActive || isUnlocked) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        TextSecondary
                    },
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
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = title,
                        color = if (isActive || isUnlocked) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            TextSecondary
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                    )
                    if (onViewRange != null) {
                        OutlinedButton(
                            onClick = onViewRange,
                            modifier = Modifier.height(38.dp),
                            shape = RoundedCornerShape(13.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                        ) {
                            Text("Voir range", fontSize = 13.sp, maxLines = 1)
                        }
                    }
                }
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
        "${mastery.answerCount}/30 réponses · ${mastery.answersRemaining} restantes"
    else ->
        "${mastery.correctCount}/30 correctes · ${mastery.successRatePercent} % · " +
            mastery.diversityLabel()
}

private fun RangeDefinition.shortDescription(): String =
    when (chapter) {
        com.pokergrind.app.domain.model.RangeChapter.OPENS -> "${stackDepthBb} BB · Open 2,5 BB"
        com.pokergrind.app.domain.model.RangeChapter.BB_DEFENSES -> "BTN open 2,5 BB · Call / 3-bet / Fold"
        com.pokergrind.app.domain.model.RangeChapter.THREE_BETS -> "${stackDepthBb} BB · 3-bet"
    }

private fun SpotMastery.diversityLabel(): String {
    val actions = distinctHandsByAction.keys
    return if (actions.contains(com.pokergrind.app.domain.model.PokerAction.OPEN)) {
        "diversité ${distinctOpenHands}/8 Open, ${distinctFoldHands}/8 Fold"
    } else {
        "couverture " + distinctHandsByAction.entries.joinToString(", ") { (action, count) ->
            "${count}/5 ${action.label}"
        }
    }
}
