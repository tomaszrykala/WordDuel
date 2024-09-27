package com.tomaszrykala.wordduel.game.processor

import android.content.Context
import com.tomaszrykala.wordduel.MainDispatcherRule
import com.tomaszrykala.wordduel.game.board.Tile
import com.tomaszrykala.wordduel.game.state.Guess
import com.tomaszrykala.wordduel.repository.WordRepository
import io.mockk.coEvery
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
}