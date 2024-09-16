package com.tomaszrykala.wordduel.game.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tomaszrykala.wordduel.game.state.GameState
import com.tomaszrykala.wordduel.ui.theme.WordDuelTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordDuelTheme {
                val state: GameState by viewModel.state.collectAsState()
                GameBoardScreen(
                    state = state,
                    onKeyTileClick = viewModel::onKeyTileClick,
                    onNewGuess = viewModel::updateGameState, // onNewGuess
                )
            }
        }
    }
}

