package com.pokergrind.app.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokergrind.app.domain.model.HandMatrix
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.domain.statistics.HandStatistics
import com.pokergrind.app.domain.statistics.StatisticsSnapshot
import com.pokergrind.app.domain.training.SpotMastery
import com.pokergrind.app.ui.theme.Error
import com.pokergrind.app.ui.theme.MasteryMedium
import com.pokergrind.app.ui.theme.RangeFold
import com.pokergrind.app.ui.theme.Success
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun StatisticsScreen(
    ranges: List<RangeDefinition>,
    statistics: StatisticsSnapshot,
    masteryBySpot: Map<String, SpotMastery>,
    unlockedSpotIds: Set<String>,
    onBack: () -> Unit,
) {
    val knownRanges = ranges.filter { it.id in unlockedSpotIds }
    var selectedSpotId by rememberSaveable {
        mutableStateOf(knownRanges.firstOrNull()?.id.orEmpty())
    }
    val selectedRange = knownRanges.firstOrNull { it.id == selectedSpotId }
        ?: knownRanges.firstOrNull()
    val reliableHands = statistics.guidedHandStats.filter { it.answerCount >= 2 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("STATISTIQUES", color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text("Ta carte de maîtrise", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Chaque case se remplit selon tes réponses guidées.",
                color = TextSecondary,
            )
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                knownRanges.forEach { range ->
                    FilterChip(
                        selected = range.id == selectedRange?.id,
                        onClick = { selectedSpotId = range.id },
                        label = { Text(range.position) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                }
            }

            selectedRange?.let { range ->
                Spacer(Modifier.height(12.dp))
                val stat = statistics.guidedSpotStats.firstOrNull { it.spotId == range.id }
                SpotStatisticsCard(
                    title = range.title,
                    answers = stat?.answerCount ?: 0,
                    successRate = stat?.successRatePercent ?: 0,
                    averageTimeMillis = stat?.averageResponseTimeMillis ?: 0,
                    mastery = masteryBySpot[range.id],
                )
                Spacer(Modifier.height(16.dp))
                MasteryMatrix(
                    handStats = statistics.guidedHandStats
                        .filter { it.spotId == range.id }
                        .associateBy(HandStatistics::handNotation),
                )
            }

            Spacer(Modifier.height(24.dp))
            HandList(
                title = "À travailler",
                hands = reliableHands
                    .sortedWith(
                        compareBy(HandStatistics::successRatePercent)
                            .thenByDescending(HandStatistics::answerCount),
                    )
                    .take(5),
                ranges = ranges,
                accentWeak = true,
            )
            Spacer(Modifier.height(20.dp))
            HandList(
                title = "Points forts",
                hands = reliableHands
                    .sortedWith(
                        compareByDescending(HandStatistics::successRatePercent)
                            .thenByDescending(HandStatistics::answerCount),
                    )
                    .take(5),
                ranges = ranges,
                accentWeak = false,
            )
            Spacer(Modifier.height(20.dp))

            val freeAnswers = statistics.freeSpotStats.sumOf { it.answerCount }
            Text("Entraînement libre", style = MaterialTheme.typography.headlineMedium)
            Text(
                "$freeAnswers réponses libres · séparées de la certification",
                color = TextSecondary,
            )
            Spacer(Modifier.height(18.dp))
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text("Retour")
        }
    }
}

@Composable
private fun MasteryMatrix(handStats: Map<String, HandStatistics>) {
    Text("Maîtrise par main", style = MaterialTheme.typography.headlineMedium)
    Text(
        "Vert ≥ 90 % · orange 80–89 % · rouge < 80 % · gris : jamais vue",
        color = TextSecondary,
        fontSize = 13.sp,
    )
    Spacer(Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        HandMatrix.allHands.chunked(13).forEach { rowHands ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                rowHands.forEach { hand ->
                    val statistic = handStats[hand.notation]
                    MasteryCell(
                        notation = hand.notation,
                        mastery = statistic?.successRatePercent ?: 0,
                        hasAnswers = statistic != null,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MasteryCell(
    notation: String,
    mastery: Int,
    hasAnswers: Boolean,
    modifier: Modifier = Modifier,
) {
    val masteryColor = when {
        !hasAnswers -> RangeFold
        mastery >= 90 -> Success
        mastery >= 80 -> MasteryMedium
        else -> Error
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(RangeFold)
            .then(
                if (hasAnswers) {
                    Modifier.border(1.dp, masteryColor.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                } else {
                    Modifier
                },
            ),
    ) {
        if (hasAnswers && mastery > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(mastery.coerceIn(0, 100) / 100f)
                    .background(masteryColor.copy(alpha = 0.9f)),
            )
        }
        Text(
            text = notation,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Composable
private fun SpotStatisticsCard(
    title: String,
    answers: Int,
    successRate: Int,
    averageTimeMillis: Long,
    mastery: SpotMastery?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(
                    if (mastery?.isMastered == true) "Maîtrisé" else "${mastery?.answerCount ?: 0}/30",
                    color = if (mastery?.isMastered == true) Success else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("$successRate % de réussite · $answers réponses guidées")
            Text(
                if (answers == 0) {
                    "Temps moyen : —"
                } else {
                    String.format(
                        Locale.FRANCE,
                        "Temps moyen : %.1f s",
                        averageTimeMillis / 1_000.0,
                    )
                },
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun HandList(
    title: String,
    hands: List<HandStatistics>,
    ranges: List<RangeDefinition>,
    accentWeak: Boolean,
) {
    Text(title, style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(8.dp))
    if (hands.isEmpty()) {
        Text("Encore trop peu de réponses pour établir ce classement.", color = TextSecondary)
        return
    }

    hands.forEach { hand ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(hand.handNotation, fontWeight = FontWeight.Bold)
                Text(
                    "${ranges.firstOrNull { it.id == hand.spotId }?.position ?: hand.spotId} · " +
                        "${hand.answerCount} réponses",
                    color = TextSecondary,
                )
            }
            Text(
                "${hand.successRatePercent} %",
                color = if (accentWeak) Error else Success,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
