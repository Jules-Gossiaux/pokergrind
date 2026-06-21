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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.ui.theme.Progress
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    range: RangeDefinition,
    onStartTraining: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 36.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "PokerGrind",
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = "Les bons choix. Jusqu’au réflexe.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "0",
                    label = "XP",
                    accent = Progress,
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "0",
                    label = "Série",
                    accent = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Ton parcours",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
            ) {
                Column(Modifier.padding(22.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "1",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Column {
                            Text(range.title, style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = "${range.stackDepthBb} BB · Open ${range.sizingBb} BB",
                                color = TextSecondary,
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Apprends la première range du parcours avec des décisions Open ou Fold.",
                        color = TextSecondary,
                    )
                }
            }
        }

        Button(
            onClick = onStartTraining,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text("Commencer l’entraînement")
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(value, color = accent, style = MaterialTheme.typography.headlineMedium)
            Text(label, color = TextSecondary)
        }
    }
}
