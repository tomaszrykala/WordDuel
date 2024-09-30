package com.tomaszrykala.wordduel.game.state

import com.tomaszrykala.wordduel.game.board.Board
import com.tomaszrykala.wordduel.game.board.BoardRow
import com.tomaszrykala.wordduel.game.board.KeyTiles

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

