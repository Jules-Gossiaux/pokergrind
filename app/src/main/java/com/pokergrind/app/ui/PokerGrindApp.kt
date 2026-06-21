package com.pokergrind.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pokergrind.app.data.BtnOpenRange
import com.pokergrind.app.ui.home.HomeScreen
import com.pokergrind.app.ui.training.TrainingScreen

private enum class Destination {
    HOME,
    TRAINING,
}

@Composable
fun PokerGrindApp() {
    var destination by remember { mutableStateOf(Destination.HOME) }

    when (destination) {
        Destination.HOME -> HomeScreen(
            range = BtnOpenRange.definition,
            onStartTraining = { destination = Destination.TRAINING },
        )

        Destination.TRAINING -> TrainingScreen(
            range = BtnOpenRange.definition,
            onBack = { destination = Destination.HOME },
        )
    }
}
