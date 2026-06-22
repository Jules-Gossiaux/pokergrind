package com.pokergrind.app.ui.home

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokergrind.app.ui.theme.Progress
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.SurfaceSoft
import com.pokergrind.app.ui.theme.TextSecondary

@Composable
fun FoundationsScreen(
    xp: Int,
    streak: Int,
    masteredOpenSpots: Int,
    unlockedOpenSpots: Int,
    hasGuidedSession: Boolean,
    hasFreeSession: Boolean,
    onOpenRanges: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
) {
    var confirmImport by remember { mutableStateOf(false) }

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
            .padding(horizontal = 22.dp, vertical = 34.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            "POKERGRIND",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.2.sp,
        )
        Spacer(Modifier.height(5.dp))
        Text("Tes fondations.", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Avance chapitre par chapitre, jusqu’à transformer chaque décision en réflexe.",
            color = TextSecondary,
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
            subtitle = "$unlockedOpenSpots/5 spots débloqués · $masteredOpenSpots maîtrisés",
            detail = when {
                hasGuidedSession && hasFreeSession -> "Sessions guidée et libre en cours"
                hasGuidedSession -> "Session guidée en cours"
                hasFreeSession -> "Session libre en cours"
                else -> "BTN, CO, HJ, UTG et SB"
            },
            enabled = true,
            onClick = onOpenRanges,
        )
        Spacer(Modifier.height(12.dp))
        ChapterCard(
            title = "Défenses BB",
            subtitle = "À venir",
            detail = "Débloqué après les ranges d’open",
            enabled = false,
        )
        Spacer(Modifier.height(12.dp))
        ChapterCard(
            title = "3-bets",
            subtitle = "À venir",
            detail = "Construire puis défendre les ranges de 3-bet",
            enabled = false,
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
}

@Composable
private fun ChapterCard(
    title: String,
    subtitle: String,
    detail: String,
    enabled: Boolean,
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
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(
                    if (enabled) "Ouvrir" else "Verrouillé",
                    color = if (enabled) MaterialTheme.colorScheme.primary else TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(7.dp))
            Text(subtitle, color = if (enabled) MaterialTheme.colorScheme.primary else TextSecondary)
            Text(detail, color = TextSecondary, fontSize = 14.sp)
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
            Text(label, color = TextSecondary)
        }
    }
}
