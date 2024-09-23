package com.tomaszrykala.wordduel.game.main.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomaszrykala.wordduel.BoardTile
import com.tomaszrykala.wordduel.R
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.state.GameState
import com.tomaszrykala.wordduel.ui.theme.WordDuelTheme
import com.tomaszrykala.wordduel.ui.theme.wordDuelTitleStyle

@Composable
fun GameBoardScreen(
    modifier: Modifier = Modifier,
    state: GameState = GameState(),
    onKeyTileClick: (k: KeyTile) -> Unit = {},
    onNewGameClick: () -> Unit = {},
    onNewGuess: () -> Unit = {},
    onStart: (context: Context) -> Unit = {},
) {
    val context = LocalContext.current
    LaunchedEffect(state.isStarting) {
        onStart(context)
    }

    LaunchedEffect(state.guess) {
        onNewGuess()
    }

    BoxWithConstraints(
        modifier = modifier
            .background(Color.Gray)
            .fillMaxSize()
    ) {
        if (maxWidth < MAX_WIDTH.dp) {
            GameBoardPortrait(state, onKeyTileClick, onNewGameClick)
        } else {
            GameBoardLandscape(state, onKeyTileClick, onNewGameClick)
        }

        if (state.isLoading) {
            DictionaryLoadingDialog()
        }

        if (state.isEnded) {
            GameEndedDialog(
                word = state.word.tilesAsWord, attempt = state.board.attemptCount, isGuessed = state.isGuessed
            )
        }
    }
}

@Composable
private fun GameBoardPortrait(
    state: GameState,
    onKeyTileClick: (k: KeyTile) -> Unit,
    onNewGameClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .scrollable(state = scrollState, orientation = Orientation.Vertical, enabled = true)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.app_name),
            style = wordDuelTitleStyle,
        )

        BoardRowsSection(state)

        KeyboardAndNewGameSection(state, onKeyTileClick, onNewGameClick)
    }
}

@Composable
private fun GameBoardLandscape(
    state: GameState,
    onKeyTileClick: (k: KeyTile) -> Unit,
    onNewGameClick: () -> Unit,
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentSize(Alignment.Center)
                .weight(0.5f),
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.app_name),
                style = wordDuelTitleStyle,
            )

            BoardRowsSection(state)
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentSize(Alignment.Center)
                .weight(0.5f)
        ) {
            KeyboardAndNewGameSection(state, onKeyTileClick, onNewGameClick)
        }
    }
}

@Composable
private fun BoardRowsSection(state: GameState) {
    Spacer(modifier = Modifier.padding(16.dp))

    state.board.boardRows.forEach { boardRow ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            boardRow.tiles.forEach { tile ->
                Column { BoardTile(tile, state.nonWordEntered) }
            }
        }
    }

    Spacer(modifier = Modifier.padding(16.dp))
}

@Composable
private fun KeyboardAndNewGameSection(
    state: GameState,
    onKeyTileClick: (k: KeyTile) -> Unit,
    onNewGameClick: () -> Unit
) {
    SoftKeyboard(state.keyTiles, onKeyTileClick)

    if (state.isEnded) {
        Button(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            shape = ButtonDefaults.elevatedShape,
            onClick = onNewGameClick
        ) { Text("New Game") }
    }
}

private const val MAX_WIDTH = 600 // hardcoded, best way?

// ** ---- PREVIEWS ---- ** //

@Preview
@Composable
fun ClearGameBoardPreview() {
    WordDuelTheme {
        GameBoardScreen()
    }
}

@Preview
@Composable
fun EndedLostGameBoardPreview() {
    WordDuelTheme {
        GameBoardScreen(state = GameState().copy(isEnded = true))
    }
}

@Preview
@Composable
fun EndedWonGameBoardPreview() {
    WordDuelTheme {
        GameBoardScreen(state = GameState().copy(isEnded = true, isGuessed = true))
    }
}

@Preview
@Composable
fun LoadingGameBoardPreview() {
    WordDuelTheme {
        GameBoardScreen(state = GameState().copy(isLoading = true))
    }
}

@Preview(device = Devices.TABLET)
@Composable
fun ClearGameBoardLandscapePreview() {
    WordDuelTheme {
        GameBoardScreen()
    }
}

@Preview(device = Devices.TABLET)
@Composable
fun EndedWonGameBoardLandscapePreview() {
    WordDuelTheme {
        GameBoardScreen(state = GameState().copy(isEnded = true, isGuessed = true))
    }
}
