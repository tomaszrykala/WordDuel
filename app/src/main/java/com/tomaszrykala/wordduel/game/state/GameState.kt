package com.tomaszrykala.wordduel.game.state

import com.tomaszrykala.wordduel.game.board.Board
import com.tomaszrykala.wordduel.game.board.BoardRow
import com.tomaszrykala.wordduel.game.board.boardRowFromString
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.keyboard.createKeyTiles

data class GameState(
    val board: Board = Board(),
    val word: BoardRow = boardRowFromString("hello"),
    val keyTiles: KeyTiles = KeyTiles(),
    val guess: Guess = Guess(),
    val nonWordEntered: Boolean = false,

    val isLoading: Boolean = false,
    val isEnded: Boolean = board.isEnded,
    val isGuessed: Boolean = board.isGuessed,
)

data class Guess(val guess: List<String> = listOf()) {
    fun isFull(): Boolean = guess.size == 5
    fun isGuessNotEmpty(): Boolean = with(guess) { isFull() && this.all { it != "" } }
    fun guessAsString(): String = guess.joinToString(separator = "") { it }.lowercase()
}

data class KeyTiles(val keyTiles: List<List<KeyTile>> = createKeyTiles())

fun List<String>.isGuessNotEmpty(): Boolean = this.size == 5 && this.all { it != "" }