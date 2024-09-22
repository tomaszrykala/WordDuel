package com.tomaszrykala.wordduel.game.main

import androidx.lifecycle.ViewModel
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

    private var currentGuess = Guess()

    fun onKeyTileClick(keyTile: KeyTile) {
        val guess: List<String> = currentGuess.guess
        if (KEY_DEL == keyTile.key) {
            if (guess.isNotEmpty()) {
                currentGuess = Guess(guess.subList(0, guess.size - 1))
            }
        } else {
            currentGuess = Guess(guess + keyTile.key)
        }
        _state.value = _state.value.copy(guess = currentGuess)
    }

    private fun clearCurrentGuess() {
        if (currentGuess.isFull()) {
            currentGuess = Guess()
            _state.value = _state.value.copy(guess = currentGuess)
        }
    }

    fun updateGameState() {
        val guess = state.value.guess
        val gameState: GameState = state.value
        val processed: GameState = guessProcessor.processGuess(gameState)
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
            _state.value = processed.copy(
                board = board.copy(boardRows = newBoardRows),
                keyTiles = processed.keyTiles,
                word = processed.word
            )

        } else {
            _state.value = processed.copy(
                keyTiles = processed.keyTiles,
                word = processed.word
            )
        }

        if (guess.isGuessNotEmpty()) {
            clearCurrentGuess()
        }
    }
}