package com.tomaszrykala.wordduel.game.main

import androidx.lifecycle.ViewModel
import com.tomaszrykala.wordduel.game.board.Board
import com.tomaszrykala.wordduel.game.board.BoardRow
import com.tomaszrykala.wordduel.game.board.Tile
import com.tomaszrykala.wordduel.game.keyboard.KEY_DEL
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.processor.GuessProcessor
import com.tomaszrykala.wordduel.game.state.GameState
import com.tomaszrykala.wordduel.game.state.Guess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val guessProcessor: GuessProcessor,
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state

    private var currentGuess = mutableListOf<String>() // or `= Guess()`

    fun onKeyTileClick(keyTile: KeyTile) {
        if (KEY_DEL == keyTile.key) {
            if (currentGuess.isNotEmpty()) {
                currentGuess = currentGuess.subList(0, currentGuess.size - 1)
            }
        } else {
            currentGuess = (currentGuess + keyTile.key).toMutableList()
        }
        if (currentGuess.size == 5) {
            currentGuess = mutableListOf()
        }
        _state.value = _state.value.copy(guess = Guess(currentGuess))
    }

     fun updateGameState() {
        val guess = state.value.guess
        val stateWithCachedKeyTiles = state.value
        // val stateWithCachedKeyTiles = state.value.copy(keyTiles = keyTiles)
        val processed: GameState = guessProcessor.processGuess(stateWithCachedKeyTiles, guess)
        // val processedKeyTiles = processed.keyTiles
        if (guess.isGuessNotEmpty()) {
            // TODO save progress?
        } else {
            val board = processed.board
            val indexOfActive = board.boardRows.indexOfFirst { it.isActive }
            if (indexOfActive != -1) {
                val tiles = mutableListOf<Tile>()
                for (index in 0..4) {
                    val guessChars = guess.guess
                    if (index < guessChars.size && guessChars[index].isNotEmpty()) {
                        tiles.add(Tile.Active(guessChars[index].last()))
                    } else {
                        tiles.add(Tile.Active())
                    }
                }

                val updatedBoardRow = board.boardRows[indexOfActive].copy(
                    tile0 = tiles[0], tile1 = tiles[1], tile2 = tiles[2], tile3 = tiles[3], tile4 = tiles[4]
                )
                val newBoardRows: List<BoardRow> = board.boardRows.mapIndexed { index, boardRow ->
                    if (index == indexOfActive) updatedBoardRow else boardRow
                }
                val newBoard: Board = board.copy(boardRows = newBoardRows)
                _state.value = processed.copy(board = newBoard)

            } else {
                _state.value = processed
            }
        }
    }
}