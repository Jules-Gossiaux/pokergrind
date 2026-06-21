package com.pokergrind.app.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.data.CoOpenRange
import com.pokergrind.app.domain.statistics.HandStatistics
import com.pokergrind.app.domain.statistics.StatisticsSnapshot
import com.pokergrind.app.domain.training.SpotMastery
import com.pokergrind.app.ui.theme.Error
import com.pokergrind.app.ui.theme.Success
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun StatisticsScreen(
    statistics: StatisticsSnapshot,
    masteryBySpot: Map<String, SpotMastery>,
    unlockedSpotIds: Set<String>,
    onBack: () -> Unit,
) {
    val knownRanges = listOf(BtnOpenRange.definition, CoOpenRange.definition)
        .filter { it.id in unlockedSpotIds }
    val reliableHands = statistics.guidedHandStats.filter { it.answerCount >= 2 }
    val weakestHands = reliableHands
        .sortedWith(compareBy(HandStatistics::successRatePercent).thenByDescending(HandStatistics::answerCount))
        .take(5)
    val strongestHands = reliableHands
        .sortedWith(compareByDescending(HandStatistics::successRatePercent).thenByDescending(HandStatistics::answerCount))
        .take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp, vertical = 34.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("STATISTIQUES", color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("Ton niveau actuel", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Les taux de maîtrise utilisent uniquement les sessions guidées.",
                color = TextSecondary,
            )
            Spacer(Modifier.height(24.dp))

            knownRanges.forEach { range ->
                val stat = statistics.guidedSpotStats.firstOrNull { it.spotId == range.id }
                val mastery = masteryBySpot[range.id]
                SpotStatisticsCard(
                    title = range.title,
                    answers = stat?.answerCount ?: 0,
                    successRate = stat?.successRatePercent ?: 0,
                    averageTimeMillis = stat?.averageResponseTimeMillis ?: 0,
                    mastery = mastery,
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(18.dp))
            HandList(
                title = "À travailler",
                hands = weakestHands,
                accentWeak = true,
            )
            Spacer(Modifier.height(22.dp))
            HandList(
                title = "Points forts",
                hands = strongestHands,
                accentWeak = false,
            )
            Spacer(Modifier.height(22.dp))

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
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text("Retour")
        }
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
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
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
            Spacer(Modifier.height(10.dp))
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
    accentWeak: Boolean,
) {
    Text(title, style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(10.dp))
    if (hands.isEmpty()) {
        Text("Encore trop peu de réponses pour établir ce classement.", color = TextSecondary)
        return
    }

    hands.forEach { hand ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(hand.handNotation, fontWeight = FontWeight.Bold)
                Text(
                    "${spotLabel(hand.spotId)} · ${hand.answerCount} réponses",
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

private fun spotLabel(spotId: String): String = when (spotId) {
    BtnOpenRange.definition.id -> "BTN"
    CoOpenRange.definition.id -> "CO"
    else -> spotId
}
