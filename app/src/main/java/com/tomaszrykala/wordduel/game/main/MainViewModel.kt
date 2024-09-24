package com.tomaszrykala.wordduel.game.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomaszrykala.wordduel.game.board.BoardRow
import com.tomaszrykala.wordduel.game.board.Tile
import com.tomaszrykala.wordduel.game.board.boardRowFromString
import com.tomaszrykala.wordduel.game.keyboard.KEY_DEL
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.processor.GuessProcessor
import com.tomaszrykala.wordduel.game.state.GameState
import com.tomaszrykala.wordduel.game.state.Guess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val guessProcessor: GuessProcessor,
) : ViewModel() {

    private val _state: MutableStateFlow<GameState> = MutableStateFlow(GameState.Init)
    val state: StateFlow<GameState> = _state

    private var currentGuess = Guess()

    fun onStart(context: Context) { // TODO Get rid of Context, inject into the Repo.
        if (_state.value is GameState.Init) {
            _state.value = GameState.Loading
            viewModelScope.launch {
                guessProcessor.init(context).fold(
                    onSuccess = {
                        val word = guessProcessor.randomWord()
                        _state.value = GameState.InProgress(word = boardRowFromString(word))
                    },
                    onFailure = { _state.value = GameState.Error(it) }
                )
            }
        }
    }

    fun onKeyTileClick(keyTile: KeyTile) {
        if (state.value is GameState.InProgress) {
            val guess: List<String> = currentGuess.guess
            if (KEY_DEL == keyTile.key) {
                if (guess.isNotEmpty()) {
                    currentGuess = Guess(guess.subList(0, guess.size - 1))
                }
            } else {
                currentGuess = Guess(guess + keyTile.key)
            }
            _state.value = (_state.value as GameState.InProgress).copy(guess = currentGuess)
        }
    }

    fun onNewGameClick() {
        currentGuess = Guess()
        val word = guessProcessor.randomWord()
        _state.value = GameState.InProgress(word = boardRowFromString(word))
    }

    fun onRetry() {
        _state.value = GameState.Init
    }

    fun onNextGuess() {
        if (state.value is GameState.InProgress) {
            val value = state.value as GameState.InProgress
            val guess = value.guess
            val processed: GameState.InProgress = guessProcessor.processGuess(value)
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

            if (guess.isFull()) {
                currentGuess = Guess()
                _state.value = (_state.value as GameState.InProgress).copy(guess = currentGuess)
            }
        }
    }
}