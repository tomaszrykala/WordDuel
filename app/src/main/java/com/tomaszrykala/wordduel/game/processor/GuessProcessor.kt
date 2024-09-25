package com.tomaszrykala.wordduel.game.processor

import android.content.Context
import androidx.annotation.VisibleForTesting
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
import com.tomaszrykala.wordduel.repository.WordRepository
import javax.inject.Inject

class GuessProcessor @Inject constructor(
    private val wordRepository: WordRepository
) {

    suspend fun init(context: Context): Result<Unit> = wordRepository.initDictionary(context)

    fun randomWord(): String = wordRepository.randomWord()

    fun onNextGuess(value: GameState.InProgress): GameState.InProgress {
        val processed: GameState.InProgress = processGuess(value)
        val board = processed.board

        val indexOfActive = board.boardRows.indexOfFirst { it.isActive }
        if (indexOfActive != -1) {
            val newTiles = getNewTiles(value.guess)
            val newBoardRows = getNewBoardRows(board, indexOfActive, newTiles)
            return processed.copy(
                board = board.copy(boardRows = newBoardRows),
                keyTiles = processed.keyTiles,
                word = processed.word
            )
        } else {
            return processed.copy(keyTiles = processed.keyTiles, word = processed.word)
        }
    }

    @VisibleForTesting
    internal fun getNewTiles(guess: Guess): MutableList<Tile> {
        val tiles = mutableListOf<Tile>()
        for (index in 0..4) {
            val guessChars = guess.guess
            if (index < guessChars.size && guessChars[index].isNotEmpty()) {
                tiles.add(Tile.Active(guessChars[index].last()))
            } else {
                tiles.add(Tile.Active())
            }
        }
        return tiles
    }

    @VisibleForTesting
    internal fun getNewBoardRows(board: Board, indexOfActive: Int, tiles: List<Tile>): List<BoardRow> {
        val updatedBoardRow = board.boardRows[indexOfActive].copy(
            tile0 = tiles[0], tile1 = tiles[1], tile2 = tiles[2], tile3 = tiles[3], tile4 = tiles[4]
        )
        return board.boardRows.mapIndexed { index, boardRow ->
            if (index == indexOfActive) updatedBoardRow else boardRow
        }
    }

    private fun processGuess(inProgress: GameState.InProgress): GameState.InProgress {
        return if (inProgress.word.isGuessed && inProgress.board.isFull) {
            inProgress
        } else if (!wordRepository.searchWord(inProgress.guess.asString())) {
            processNonWord(inProgress)
        } else {
            processWord(inProgress)
        }
    }

    private fun processWord(inProgress: GameState.InProgress): GameState.InProgress {
        val word = inProgress.word
        val currentBoard = inProgress.board
        val boardRows = currentBoard.boardRows
        val nextBoardRows = boardRows.toMutableList()

        val (processedBoardRow, newKeyTiles) = processBoardRow(word, inProgress.guess, inProgress.keyTiles)
        val activeRowIndex = getActiveRowIndex(currentBoard, boardRows)

        return if (ifSameAsThePreviousRow(activeRowIndex, processedBoardRow, boardRows)) {
            inProgress
        } else {
            nextBoardRows[activeRowIndex] = processedBoardRow

            // make the next one row active, unless the word has been guessed
            if (activeRowIndex + 1 < nextBoardRows.size) {
                if (!processedBoardRow.isGuessed) {
                    nextBoardRows[activeRowIndex + 1] = emptyActiveBoardRow()
                }
            }
            GameState.InProgress(word = word, board = Board(nextBoardRows), keyTiles = KeyTiles(newKeyTiles))
        }
    }

    private fun getActiveRowIndex(currentBoard: Board, boardRows: List<BoardRow>): Int =
        if (currentBoard.isFull) boardRows.lastIndex else boardRows.indexOfFirst { it.isActive }

    private fun ifSameAsThePreviousRow(
        activeRowIndex: Int, processedBoardRow: BoardRow, boardRows: List<BoardRow>
    ) = activeRowIndex > 0 && processedBoardRow == boardRows[activeRowIndex - 1]

    private fun processNonWord(state: GameState.InProgress): GameState.InProgress {
        return if (state.guess.isFull()) {
            val currentBoard = state.board
            val boardRows = currentBoard.boardRows
            val indexOfActive = boardRows.indexOfFirst { it.isActive }
            val nextBoardRows = boardRows.toMutableList()
            nextBoardRows[indexOfActive] = emptyActiveBoardRow()

            state.copy(guess = Guess(), board = Board(nextBoardRows), nonWordEntered = true)
        } else state
    }

    private fun processBoardRow(
        word: BoardRow, guess: Guess, keyTiles: KeyTiles
    ): Pair<BoardRow, List<List<KeyTile>>> {

        val tilesAsWord = word.tilesAsWord
        val mutableKeyTiles = keyTiles.keyTiles.map { it.toMutableList() }

        val processed: List<Tile> = guess.guess.mapIndexed { index, letter ->
            val char: Char = letter.last()

            when {
                tilesAsWord.contains(letter).not() -> {
                    remapKeyTileColor(mutableKeyTiles, letter, Color.DarkGray)
                    Tile.Miss(char)
                } // black
                word.tiles[index].letter() == letter -> {
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

        val boardRow = with(processed) {
            BoardRow(tile0 = this[0], tile1 = this[1], tile2 = this[2], tile3 = this[3], tile4 = this[4])
        }
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
