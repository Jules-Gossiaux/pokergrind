package com.pokergrind.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pokergrind.app.ui.PokerGrindApp
import com.pokergrind.app.ui.theme.PokerGrindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokerGrindTheme {
                PokerGrindApp()
            }
        }
    }
}
