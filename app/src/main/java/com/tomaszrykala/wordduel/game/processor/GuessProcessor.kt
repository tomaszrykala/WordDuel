package com.tomaszrykala.wordduel.game.processor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.tomaszrykala.wordduel.game.board.Board
import com.tomaszrykala.wordduel.game.board.BoardRow
import com.tomaszrykala.wordduel.game.board.Tile
import com.tomaszrykala.wordduel.game.board.emptyActiveBoardRow
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.state.GameState
import com.tomaszrykala.wordduel.game.state.Guess
import com.tomaszrykala.wordduel.game.state.KeyTiles
import com.tomaszrykala.wordduel.game.state.isGuessNotEmpty

class GuessProcessor { // @Inject constructor() // add an interface?

    private val dictionary: Trie = Trie()

    init {
        // TODO dummy data; initDictionary()
        with(dictionary) {
            insert("hello")
            insert("world")
        }
    }

    fun processGuess(state: GameState, guess: Guess): GameState {
        return if (state.word.isGuessed && state.board.boardRows.none { it.isActive }) {
            state
        } else if (!dictionary.search(guess.guessAsString())) {
            processNonWord(guess, state)
        } else {
            processWord(state, guess)
        }
    }

    private fun processWord(state: GameState, guess: Guess): GameState {
        val word = state.word
        val keyTiles = state.keyTiles
        val currentBoard = state.board
        val nextBoardRows = currentBoard.boardRows.toMutableList()

        val (processedBoard, newKeyTiles) = processBoard(word, guess, keyTiles)

        // make the previous row inactive
        val indexOfInactive = if (currentBoard.isFull) {
            currentBoard.boardRows.lastIndex
        } else {
            currentBoard.boardRows.indexOfFirst { it.isActive }
        }

        // prevent duplication on configuration change
        return if (indexOfInactive > 0 && processedBoard == currentBoard.boardRows[indexOfInactive - 1]) {
            state
        } else {
            nextBoardRows[indexOfInactive] = processedBoard

            // make the next one row active, unless the word has been guessed
            if (indexOfInactive + 1 < nextBoardRows.size) {
                if (!processedBoard.isGuessed) {
                    nextBoardRows[indexOfInactive + 1] = emptyActiveBoardRow()
                }
            }
            GameState(
                word = word,
                board = Board(nextBoardRows),
                keyTiles = KeyTiles(newKeyTiles),
//                wordIndex = state.wordIndex,
//                isStale = state.isStale,
//                isUnlimited = state.isUnlimited
            )
        }
    }

    private fun processNonWord(guess: Guess, state: GameState): GameState {
        return if (guess.isGuessNotEmpty()) {
            val currentBoard = state.board
            val indexOfActive = currentBoard.boardRows.indexOfFirst { it.isActive }
            val nextBoardRows = currentBoard.boardRows.toMutableList()
            nextBoardRows[indexOfActive] = emptyActiveBoardRow()

            state.copy(
                guess = Guess(),
                board = Board(nextBoardRows),
                nonWordEntered = true // TODO careful, could be dangerous (?)
            )
        } else {
            state
        }
    }


    private fun processBoard(
        word: BoardRow, guess: Guess, keyTiles: KeyTiles
    ): Pair<BoardRow, List<List<KeyTile>>> {

        val mutableKeyTiles = keyTiles.keyTiles.map { it.toMutableList() } // TODO AWFUL complexity
        val tilesAsWord = word.tilesAsWord

        val processed: List<Tile> = guess.guess.mapIndexed { index, letter ->
            val char: Char = letter.last()

            when {
                tilesAsWord.contains(letter).not() -> {
                    remapKeyTileColor(mutableKeyTiles, letter, Color.DarkGray)
                    Tile.Miss(char)
                } // black
                word.tiles[index].char() == letter -> {
                    remapKeyTileColor(mutableKeyTiles, letter, Color.Green)
                    Tile.Hit(char)
                } // green
                tilesAsWord.contains(letter) -> {
                    remapKeyTileColor(mutableKeyTiles, letter, Color.Yellow)
                    Tile.Misplaced(char)
                } // yellow
                else -> Tile.Miss(char)
            }
        }

        val boardRow = BoardRow(
            tile0 = processed[0],
            tile1 = processed[1],
            tile2 = processed[2],
            tile3 = processed[3],
            tile4 = processed[4]
        )
        return boardRow to mutableKeyTiles
    }

    private fun remapKeyTileColor(
        mutableKeyTiles: List<MutableList<KeyTile>>, letter: String, color: Color
    ) {
        mutableKeyTiles.forEach { list ->
            list.forEachIndexed { index, keyTile ->
                if (keyTile.key == letter) {
                    list[index] = KeyTile(keyTile.key, color.toArgb())
                    return@forEach
                }
            }
        }
    }
}
