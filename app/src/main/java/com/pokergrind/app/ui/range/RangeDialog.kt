package com.pokergrind.app.ui.range

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pokergrind.app.domain.model.HandCategory
import com.pokergrind.app.domain.model.PokerAction
import com.pokergrind.app.domain.model.RangeDefinition
import com.pokergrind.app.ui.theme.RangeFold
import com.pokergrind.app.ui.theme.RangeOpen
import com.pokergrind.app.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun RangeDialog(
    range: RangeDefinition,
    highlightedHand: HandCategory?,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(range.title, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            text = String.format(
                                Locale.FRANCE,
                                "%.2f %% · %d combinaisons",
                                range.openPercentage,
                                range.openComboCount,
                            ),
                            color = TextSecondary,
                        )
                    }
                    Button(onClick = onDismiss) {
                        Text("Fermer")
                    }
                }

                Row(
                    modifier = Modifier.padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    LegendItem(color = RangeOpen, label = "Open")
                    LegendItem(color = RangeFold, label = "Fold")
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(13),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(range.entries, key = { it.hand.notation }) { entry ->
                        val highlighted = entry.hand.notation == highlightedHand?.notation
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(
                                    color = if (entry.action == PokerAction.OPEN) RangeOpen else RangeFold,
                                    shape = RoundedCornerShape(3.dp),
                                )
                                .then(
                                    if (highlighted) {
                                        Modifier.border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            shape = RoundedCornerShape(3.dp),
                                        )
                                    } else {
                                        Modifier
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = entry.hand.notation,
                                color = if (entry.action == PokerAction.OPEN) Color(0xFF102335) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.sp,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .background(color, RoundedCornerShape(4.dp))
                .padding(7.dp),
        )
        Text(label, color = TextSecondary)
    }
}
