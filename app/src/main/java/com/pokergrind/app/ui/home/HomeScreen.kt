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
import com.pokergrind.app.ui.theme.Progress
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.SurfaceSoft
import com.pokergrind.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    range: RangeDefinition,
    xp: Int,
    streak: Int,
    activeSession: StoredTrainingSession?,
    onStartTraining: () -> Unit,
) {
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
                text = "Le prochain spot se débloque à 90 % de réussite sur tes 30 dernières réponses.",
                color = TextSecondary,
            )

            Spacer(Modifier.height(18.dp))

            PathStep(
                number = 1,
                title = "Open BTN",
                subtitle = activeSession?.let {
                    "Session en cours · ${(it.questionIndex + 1).coerceAtMost(it.hands.size)}/${it.hands.size}"
                } ?: "${range.stackDepthBb} BB · Open 2,5 BB",
                isActive = true,
                unlockHint = "Objectif : 27 bonnes réponses sur les 30 dernières",
            )
            PathStep(number = 2, title = "Open CO", subtitle = "À débloquer")
            PathStep(number = 3, title = "Open HJ", subtitle = "À débloquer")
            PathStep(number = 4, title = "Open UTG", subtitle = "À débloquer")
            PathStep(number = 5, title = "Open SB", subtitle = "À débloquer", showConnector = false)

            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = onStartTraining,
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = if (activeSession != null) {
                    "Reprendre · ${activeSession.questionIndex + 1}/20"
                } else {
                    "Commencer la session"
                },
            )
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
    unlockHint: String? = null,
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
                        color = if (isActive) MaterialTheme.colorScheme.primary else SurfaceElevated,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = number.toString(),
                    color = if (isActive) MaterialTheme.colorScheme.onPrimary else TextSecondary,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 28.dp)
                        .background(SurfaceElevated),
                )
            }
        }

        Card(
            modifier = Modifier
                .padding(start = 12.dp, bottom = 10.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isActive) SurfaceElevated else Color.Transparent,
            ),
            shape = RoundedCornerShape(18.dp),
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 11.dp)) {
                Text(
                    text = title,
                    color = if (isActive) MaterialTheme.colorScheme.onSurface else TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                )
                Text(
                    text = subtitle,
                    color = if (isActive) MaterialTheme.colorScheme.primary else TextSecondary.copy(alpha = 0.65f),
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
