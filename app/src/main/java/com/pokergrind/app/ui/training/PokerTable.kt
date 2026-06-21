package com.pokergrind.app.ui.training

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokergrind.app.ui.theme.SurfaceElevated
import com.pokergrind.app.ui.theme.SurfaceSoft
import com.pokergrind.app.ui.theme.TextSecondary

@Composable
fun PokerTable(
    activePosition: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(166.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(108.dp)
                .clip(RoundedCornerShape(50))
                .background(SurfaceSoft)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                    shape = RoundedCornerShape(50),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "6-MAX · 100 BB",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
            )
        }

        TableSeat("UTG", activePosition, Alignment.TopStart, 30, 5)
        TableSeat("HJ", activePosition, Alignment.TopEnd, -30, 5)
        TableSeat("CO", activePosition, Alignment.CenterEnd, -3, 0)
        TableSeat("BTN", activePosition, Alignment.BottomEnd, -30, -5)
        TableSeat("SB", activePosition, Alignment.BottomStart, 30, -5)
        TableSeat("BB", activePosition, Alignment.CenterStart, 3, 0)
    }
}

@Composable
private fun BoxScope.TableSeat(
    position: String,
    activePosition: String,
    alignment: Alignment,
    horizontalOffset: Int,
    verticalOffset: Int,
) {
    val active = position == activePosition
    Box(
        modifier = Modifier
            .align(alignment)
            .offset(x = horizontalOffset.dp, y = verticalOffset.dp)
            .width(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (active) MaterialTheme.colorScheme.primary else SurfaceElevated,
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = position,
            color = if (active) MaterialTheme.colorScheme.onPrimary else TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
        )
    }
}
