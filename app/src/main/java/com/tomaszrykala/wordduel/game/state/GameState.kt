package com.tomaszrykala.wordduel.game.state

import com.tomaszrykala.wordduel.game.board.Board
import com.tomaszrykala.wordduel.game.board.BoardRow
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.keyboard.createKeyTiles

data class GameState(
    var board: Board,
    val word: BoardRow,
    val guess: List<String> = mutableListOf("", "", "", "", ""),
    val keyTiles: List<List<KeyTile>> = createKeyTiles(),
    val nonWordEntered: Boolean = false,

    // val wordIndex: Int,
    // val isStale: Boolean,
    // val isUnlimited: Boolean = false
)

fun List<String>.isGuessNotEmpty(): Boolean = this.size == 5 && this.all { it != "" }