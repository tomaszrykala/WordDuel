package com.tomaszrykala.wordduel.game.processor

import android.content.Context
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

class GuessProcessor @Inject constructor(private val wordRepository: WordRepository) {

    suspend fun init(context: Context): Result<Unit> = wordRepository.initDictionary(context)

    fun randomWord(): String = wordRepository.randomWord()

    fun onNextGuess(value: GameState.InProgress): GameState.InProgress {
        val guess = value.guess
        val processed: GameState.InProgress = processGuess(value)
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
            return processed.copy(
                board = board.copy(boardRows = newBoardRows),
                keyTiles = processed.keyTiles,
                word = processed.word
            )

        } else {
            return processed.copy(
                keyTiles = processed.keyTiles,
                word = processed.word
            )
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
        val keyTiles = inProgress.keyTiles
        val currentBoard = inProgress.board
        val boardRows = currentBoard.boardRows
        val nextBoardRows = boardRows.toMutableList()

        val (processedBoardRow, newKeyTiles) = processBoardRow(word, inProgress.guess, keyTiles)

        // make the previous row inactive
        val indexOfInactive = if (currentBoard.isFull) {
            boardRows.lastIndex
        } else {
            boardRows.indexOfFirst { it.isActive }
        }

        // prevent duplication on configuration change // TODO ?!
        return if (indexOfInactive > 0 && processedBoardRow == boardRows[indexOfInactive - 1]) {
            inProgress
        } else {
            nextBoardRows[indexOfInactive] = processedBoardRow

            // make the next one row active, unless the word has been guessed
            if (indexOfInactive + 1 < nextBoardRows.size) {
                if (!processedBoardRow.isGuessed) {
                    nextBoardRows[indexOfInactive + 1] = emptyActiveBoardRow()
                }
            }
            GameState.InProgress(
                word = word, board = Board(nextBoardRows), keyTiles = KeyTiles(newKeyTiles)
            )
        }
    }

    private fun processNonWord(state: GameState.InProgress): GameState.InProgress {
        return if (state.guess.isFull()) {
            val currentBoard = state.board
            val boardRows = currentBoard.boardRows
            val indexOfActive = boardRows.indexOfFirst { it.isActive }
            val nextBoardRows = boardRows.toMutableList()
            nextBoardRows[indexOfActive] = emptyActiveBoardRow()

            state.copy(
                guess = Guess(),
                board = Board(nextBoardRows),
                nonWordEntered = true
            )
        } else {
            state
        }
    }

    private fun processBoardRow(
        word: BoardRow, guess: Guess, keyTiles: KeyTiles
    ): Pair<BoardRow, List<List<KeyTile>>> {

        val mutableKeyTiles = keyTiles.keyTiles.map { it.toMutableList() } // TODO awful complexity
        val tilesAsWord = word.tilesAsWord

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
