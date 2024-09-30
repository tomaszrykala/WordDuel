package com.tomaszrykala.wordduel.game.processor

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.tomaszrykala.wordduel.MainDispatcherRule
import com.tomaszrykala.wordduel.game.board.Board
import com.tomaszrykala.wordduel.game.board.BoardRow
import com.tomaszrykala.wordduel.game.board.KeyTiles
import com.tomaszrykala.wordduel.game.board.Tile
import com.tomaszrykala.wordduel.game.board.hitBoardRowFromString
import com.tomaszrykala.wordduel.game.board.emptyActiveBoardRow
import com.tomaszrykala.wordduel.game.board.emptyInactiveBoardRow
import com.tomaszrykala.wordduel.game.keyboard.KEY_DEL
import com.tomaszrykala.wordduel.game.keyboard.KEY_DEL_SIZE
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.state.GameState
import com.tomaszrykala.wordduel.game.state.Guess
import com.tomaszrykala.wordduel.repository.WordRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class GuessProcessorTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private val wordRepository: WordRepository = mockk()

    private val sut = GuessProcessor(wordRepository)

    private val word = "world"

    @Test
    fun `GIVEN Success is produced WHEN init THEN return it`() = runTest {
        val context = mockk<Context>()
        coEvery { wordRepository.initDictionary(context) } returns Result.success(Unit)

        val result = sut.init(context)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `GIVEN Failure is produced WHEN init THEN return it`() = runTest {
        val context = mockk<Context>()
        val exception = IllegalStateException("agh")
        coEvery { wordRepository.initDictionary(context) } returns Result.failure(exception)

        val result = sut.init(context)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull()!!)
    }

    @Test
    fun `GIVEN a word is produced WHEN randomWord THEN return it`() = runTest {
        coEvery { wordRepository.randomWord() } returns word

        val randomWord = sut.randomWord()

        assertEquals(word, randomWord)
    }

    @Test
    fun `GIVEN Guess is empty WHEN getNewTiles THEN return empty Tiles`() {
        val newTiles = sut.getNewTiles(Guess())

        assertEquals(5, newTiles.size)
        assertTrue(newTiles.all { it == Tile.Active() })
    }

    @Test
    fun `GIVEN Guess has letters WHEN getNewTiles THEN return empty Tiles with Chars`() {
        val wordAsStringList = word.toList().map { it.toString() }
        val newTiles = sut.getNewTiles(Guess(guess = wordAsStringList))

        assertEquals(5, newTiles.size)
        wordAsStringList.forEachIndexed { index, s ->
            assertTrue(newTiles[index] == Tile.Active(s.last()))
        }
    }

    @Test
    fun `GIVEN BoardRows is empty WHEN getNewBoardRows THEN return empty BoardRows`() {
        val boardRows = emptyList<BoardRow>()

        val rows = sut.getNewBoardRows(boardRows, 5, emptyList())

        assertEquals(rows, boardRows)
    }

    @Test
    fun `GIVEN indexOfActive is larger than BoardRows WHEN getNewBoardRows THEN return the same BoardRows`() {
        val boardRows = listOf(emptyActiveBoardRow())

        val rows = sut.getNewBoardRows(boardRows, 3, emptyList())

        assertEquals(rows, boardRows)
    }

    @Test
    fun `WHEN getNewBoardRows THEN return remapped BoardRows`() {
        val boardRows = listOf(emptyActiveBoardRow(), emptyInactiveBoardRow())
        val worldAsString = BoardRow(
            tile0 = Tile.Active(word[0]),
            tile1 = Tile.Active(word[1]),
            tile2 = Tile.Active(word[2]),
            tile3 = Tile.Active(word[3]),
            tile4 = Tile.Active(word[4]),
        )

        val rows = sut.getNewBoardRows(boardRows, 0, worldAsString.tiles)

        assertEquals(listOf(worldAsString, emptyInactiveBoardRow()), rows)
    }

    @Test
    fun `GIVEN the word is guessed and the board full WHEN onNextGuess THEN return inProgress`() {
        val gameState = GameState.InProgress(
            word = hitBoardRowFromString(word),
            board = Board(listOf(emptyInactiveBoardRow()))
        )

        val result = sut.onNextGuess(gameState)

        assertEquals(gameState, result)
    }

    @Test
    fun `GIVEN a non-full non-word is entered WHEN onNextGuess THEN return inProgress`() {
        val guessWord = "HEL"
        every { wordRepository.searchWord(guessWord.lowercase()) } returns false
        val gameState = GameState.InProgress(
            word = BoardRow(
                tile0 = Tile.Active('H'),
                tile1 = Tile.Active('E'),
                tile2 = Tile.Active('L'),
                tile3 = Tile.Active(),
                tile4 = Tile.Active(),
            ),
            guess = Guess(guessWord.toList().map { it.toString() }),
            board = Board(listOf(emptyInactiveBoardRow()))
        )

        val result = sut.onNextGuess(gameState)

        assertEquals(gameState, result)
    }

    @Test
    fun `GIVEN a non-word is entered WHEN onNextGuess THEN process non-word`() {
        val guessWord = "HELLO"
        every { wordRepository.searchWord(guessWord.lowercase()) } returns false
        val gameState = getValidWordInitialState(guessWord)

        val result = sut.onNextGuess(gameState)

        val expected = gameState.copy(guess = Guess(), nonWordEntered = true)
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN a guessed valid word is entered WHEN onNextGuess THEN return processed state and set keys to Green`() {
        val guessWord = "HELLO"
        every { wordRepository.searchWord(guessWord.lowercase()) } returns true
        val gameState = getValidWordInitialState(guessWord)

        val result = sut.onNextGuess(gameState)

        val expected = getValidWordExpectedResult(guessWord, gameState, Color.Green)
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN a missed valid word is entered WHEN onNextGuess THEN return processed state and set keys to DarkGray`() {
        val guessWord = "WURST"
        every { wordRepository.searchWord(guessWord.lowercase()) } returns true
        val gameState = getValidWordInitialState(guessWord)

        val result = sut.onNextGuess(gameState)

        val expected = getValidWordExpectedResult(
            guessWord,
            gameState,
            Color.DarkGray,
            isGuessed = false,
            isEnded = false,
            guessRow = BoardRow(
                tile0 = Tile.Miss(guessWord[0]),
                tile1 = Tile.Miss(guessWord[1]),
                tile2 = Tile.Miss(guessWord[2]),
                tile3 = Tile.Miss(guessWord[3]),
                tile4 = Tile.Miss(guessWord[4]),
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN a word with misplaced Chars is entered WHEN onNextGuess THEN return processed state and set keys to Yellow`() {
        val guessWord = "LOHEL"
        every { wordRepository.searchWord(guessWord.lowercase()) } returns true
        val gameState = getValidWordInitialState(guessWord)

        val result = sut.onNextGuess(gameState)

        val expected = getValidWordExpectedResult(
            guessWord,
            gameState,
            Color.Yellow,
            isGuessed = false,
            isEnded = false,
            guessRow = BoardRow(
                tile0 = Tile.Misplaced(guessWord[0]),
                tile1 = Tile.Misplaced(guessWord[1]),
                tile2 = Tile.Misplaced(guessWord[2]),
                tile3 = Tile.Misplaced(guessWord[3]),
                tile4 = Tile.Misplaced(guessWord[4]),
            )
        )
        assertEquals(expected, result)
    }

    private fun getValidWordInitialState(guessWord: String): GameState.InProgress {
        val gameState = GameState.InProgress(
            word = BoardRow(
                tile0 = Tile.Active('H'),
                tile1 = Tile.Active('E'),
                tile2 = Tile.Active('L'),
                tile3 = Tile.Active('L'),
                tile4 = Tile.Active('O'),
            ),
            guess = Guess(guessWord.toList().map { it.toString() }),
            board = Board(
                listOf(
                    BoardRow(
                        tile0 = Tile.Active(guessWord[0]),
                        tile1 = Tile.Active(guessWord[1]),
                        tile2 = Tile.Active(guessWord[2]),
                        tile3 = Tile.Active(guessWord[3]),
                        tile4 = Tile.Active(guessWord[4]),
                    ),
                    emptyInactiveBoardRow()
                ),
            )
        )
        return gameState
    }

    private fun getValidWordExpectedResult(
        guessWord: String,
        gameState: GameState.InProgress,
        color: Color,
        isGuessed: Boolean = true,
        isEnded: Boolean = true,
        guessRow: BoardRow = hitBoardRowFromString(guessWord),
    ): GameState.InProgress {
        val function: (s: String) -> KeyTile =
            { if (guessWord.contains(it)) KeyTile(it, color = color.toArgb()) else KeyTile(it) }
        val top = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P").map { function.invoke(it) }
        val mid = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L").map { function.invoke(it) }
        val bottom = listOf("Z", "X", "C", "V", "B", "N", "M", KEY_DEL)
            .map { function.invoke(it) }
            .map { if (it.key == KEY_DEL) it.copy(width = KEY_DEL_SIZE) else it }

        val nextBoardRow = if (isGuessed) emptyInactiveBoardRow() else BoardRow(
            tile0 = Tile.Active(guessWord[0]),
            tile1 = Tile.Active(guessWord[1]),
            tile2 = Tile.Active(guessWord[2]),
            tile3 = Tile.Active(guessWord[3]),
            tile4 = Tile.Active(guessWord[4]),
        )

        return gameState.copy(
            guess = Guess(),
            isGuessed = isGuessed,
            isEnded = isEnded,
            board = Board(listOf(guessRow, nextBoardRow)),
            keyTiles = KeyTiles(listOf(top, mid, bottom))
        )
    }
}