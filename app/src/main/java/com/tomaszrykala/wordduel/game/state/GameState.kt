package com.tomaszrykala.wordduel.game.state

import com.tomaszrykala.wordduel.game.board.Board
import com.tomaszrykala.wordduel.game.board.BoardRow
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.keyboard.createKeyTiles

sealed class GameState {
    data object Init : GameState()
    data object Loading : GameState()
    data class Error(val throwable: Throwable) : GameState()
    data class InProgress(
        val board: Board = Board(),
        val guess: Guess = Guess(),
        val keyTiles: KeyTiles = KeyTiles(),
        val word: BoardRow = board.boardRows.first(),
        val isEnded: Boolean = board.isEnded,
        val isGuessed: Boolean = board.isGuessed,
        val nonWordEntered: Boolean = false
    ) : GameState()
}

data class Guess(val guess: List<String> = listOf()) {
    fun isFull(): Boolean = guess.size == 5
    fun isGuessNotEmpty(): Boolean = with(guess) { isFull() && this.all { it != "" } }
    fun guessAsString(): String = guess.joinToString(separator = "") { it }.lowercase()
}

data class KeyTiles(val keyTiles: List<List<KeyTile>> = createKeyTiles()) {
    val top get() = keyTiles[0]
    val mid get() = keyTiles[1]
    val bottom get() = keyTiles[2]
}
