package com.pokergrind.app.ui.training

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.TextSecondary

@Composable
fun FreeSpotScreen(
    ranges: List<RangeDefinition>,
    unlockedSpotIds: Set<String>,
    freeAnswerCount: Int,
    onSelectSpot: (String) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp, vertical = 34.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text("ENTRAÎNEMENT LIBRE", color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("Choisis ton spot", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Les bonnes réponses rapportent de l’XP. Les erreurs renforcent les prochaines sessions guidées.",
                color = TextSecondary,
            )
            Spacer(Modifier.height(8.dp))
            Text("$freeAnswerCount réponses libres au total", color = TextSecondary)
            Spacer(Modifier.height(28.dp))

            ranges.forEach { range ->
                val enabled = range.id in unlockedSpotIds
                SpotCard(
                    title = range.title,
                    subtitle = if (enabled) "100 BB · Open 2,5 BB" else "Verrouillé",
                    enabled = enabled,
                    onClick = { onSelectSpot(range.id) },
                )
                Spacer(Modifier.height(10.dp))
            }
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
private fun SpotCard(
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, color = TextSecondary)
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (enabled) "Jouer ce spot" else "Indisponible")
            }
        }
    }
}
