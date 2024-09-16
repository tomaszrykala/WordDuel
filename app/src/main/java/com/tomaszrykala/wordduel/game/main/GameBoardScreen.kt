package com.tomaszrykala.wordduel.game.main

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomaszrykala.wordduel.BoardTile
import com.tomaszrykala.wordduel.R
import com.tomaszrykala.wordduel.game.board.Board
import com.tomaszrykala.wordduel.game.keyboard.KEY_DEL
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.state.GameState
import com.tomaszrykala.wordduel.game.state.KeyTiles
import com.tomaszrykala.wordduel.ui.theme.WordDuelTheme
import com.tomaszrykala.wordduel.ui.theme.wordDuelTitleStyle

@Composable
fun GameBoardScreen(
    modifier: Modifier = Modifier,
    state: GameState = GameState(),
    onKeyTileClick: (k: KeyTile) -> Unit,
    onNewGuess: () -> Unit
) {

    println("CSQ guess 1: " +state.guess)
    LaunchedEffect(state.guess) {
        println("CSQ guess 2: " +state.guess)
        onNewGuess()
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(state = scrollState, orientation = Orientation.Vertical, enabled = true)
                .align(Alignment.TopCenter)
                .background(Color.Gray)
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.app_name),
                style = wordDuelTitleStyle,
            )

            Spacer(modifier = Modifier.padding(16.dp))

            BoardRows(state.board, state.nonWordEntered)

            Spacer(modifier = Modifier.padding(16.dp))

            SoftKeyboard(state.keyTiles, onKeyTileClick)

        }
    }
}

@Composable
private fun SoftKeyboard(
    keyTiles: KeyTiles,
    onKeyTileClick: (k: KeyTile) -> Unit
) {

    // CLEARS ON CFG CHANGE
    val top = keyTiles.keyTiles[0]
    val mid = keyTiles.keyTiles[1]
    val bottom = keyTiles.keyTiles[2]

    listOf(top, mid, bottom).forEach { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.Center
        ) {

            row.forEach { keyTileValue ->

                Button(
                    modifier = Modifier
                        .height(52.dp)
                        .padding(2.dp)
                        .width(if (keyTileValue.key == KEY_DEL) 46.dp else 28.dp)
                        .align(Alignment.CenterVertically),
                    elevation = ButtonDefaults.elevatedButtonElevation(),
                    shape = ShapeDefaults.ExtraSmall,
                    contentPadding = PaddingValues(2.dp),
                    colors = ButtonDefaults.textButtonColors(containerColor = Color(keyTileValue.color)),
                    onClick = { onKeyTileClick(keyTileValue) },
                ) {
                    Text(
                        text = keyTileValue.key,
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}


@Composable
private fun BoardRows(
    board: Board, nonWordEntered: Boolean
) {
    board.boardRows.forEach { boardRow ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            boardRow.tiles.forEach { tile ->
                Column { BoardTile(tile, nonWordEntered) }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GameBoardPreview() {
    WordDuelTheme {
        GameBoardScreen(
            onKeyTileClick = { },
            onNewGuess = { },
        )
    }
}
