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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.pokergrind.app.ui.theme.RangeCall
import com.pokergrind.app.ui.theme.RangeFold
import com.pokergrind.app.ui.theme.RangeOpen
import com.pokergrind.app.ui.theme.RangeThreeBet
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
            Box(Modifier.fillMaxSize()) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .size(44.dp),
                ) {
                    Text("×", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 52.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(range.title, style = MaterialTheme.typography.headlineMedium)
                            Text(
                                text = String.format(
                                    Locale.FRANCE,
                                    "%s · %d combinaisons jouées",
                                    range.playedActionsSummary(),
                                    range.entries
                                        .filter { it.action != PokerAction.FOLD }
                                        .sumOf { it.hand.comboCount },
                                ),
                                color = TextSecondary,
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        range.availableActions.forEach { action ->
                            LegendItem(color = action.rangeColor(), label = action.rangeLabel())
                        }
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
                                        color = entry.action.rangeColor(),
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
                                    color = if (entry.action == PokerAction.FOLD) Color.White else Color(0xFF102335),
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
}

private fun RangeDefinition.playedActionsSummary(): String =
    availableActions
        .filterNot { it == PokerAction.FOLD }
        .joinToString(" · ") { action ->
            String.format(Locale.FRANCE, "%s %.1f%%", action.rangeLabel(), percentageFor(action))
        }

private fun PokerAction.rangeColor(): Color =
    when (this) {
        PokerAction.OPEN -> RangeOpen
        PokerAction.CALL -> RangeCall
        PokerAction.THREE_BET -> RangeThreeBet
        PokerAction.FOLD -> RangeFold
    }

private fun PokerAction.rangeLabel(): String =
    when (this) {
        PokerAction.OPEN -> "Open"
        PokerAction.CALL -> "Call"
        PokerAction.THREE_BET -> "3-bet"
        PokerAction.FOLD -> "Fold"
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
