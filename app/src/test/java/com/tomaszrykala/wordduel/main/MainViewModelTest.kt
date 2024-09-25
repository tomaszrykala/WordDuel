package com.tomaszrykala.wordduel.main

import android.content.Context
import com.tomaszrykala.wordduel.MainDispatcherRule
import com.tomaszrykala.wordduel.game.board.boardRowFromString
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.processor.GuessProcessor
import com.tomaszrykala.wordduel.game.state.GameState
import com.tomaszrykala.wordduel.game.state.Guess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private val guessProcessor: GuessProcessor = mockk()

    private val sut = MainViewModel(guessProcessor)

    private val world = "WORLD"

    @Test
    fun `GIVEN success is returned WHEN onStart THEN set InProgress state`() = runTest {
        val context = mockk<Context>()
        coEvery { guessProcessor.init(context) } returns Result.success(Unit)
        coEvery { guessProcessor.randomWord() } returns world

        sut.onStart(context)

        assertTrue(sut.state.value is GameState.InProgress)
        assertEquals(boardRowFromString(world), (sut.state.value as GameState.InProgress).word)
    }

    @Test
    fun `GIVEN exception is thrown WHEN onStart THEN set Error state`() = runTest {
        val context = mockk<Context>()
        val exception = IllegalStateException("API")
        coEvery { guessProcessor.init(context) } returns Result.failure(exception)

        sut.onStart(context)

        assertTrue(sut.state.value is GameState.Error)
        assertEquals(exception, (sut.state.value as GameState.Error).throwable)
        coVerify(exactly = 0) { guessProcessor.randomWord() }
    }

    @Test
    fun `WHEN onNewGameClick THEN reset the game`() = runTest {
        coEvery { guessProcessor.randomWord() } returns world

        sut.onNewGameClick()

        assertTrue(sut.state.value is GameState.InProgress)
        assertEquals(boardRowFromString(world), (sut.state.value as GameState.InProgress).word)
    }

    @Test
    fun `WHEN onRetry THEN set state to Init`() = runTest {
        coEvery { guessProcessor.randomWord() } returns world
        sut.onNewGameClick()
        assertTrue(sut.state.value is GameState.InProgress)

        sut.onRetry()

        assertTrue(sut.state.value is GameState.Init)
    }

    @Test
    fun `GIVEN state is not InProgress WHEN onNextGuess THEN do not do anything`() = runTest {
        sut.onNextGuess()

        assertTrue(sut.state.value is GameState.Init)

        coVerify(exactly = 0) { guessProcessor.onNextGuess(any()) }
    }

    @Test
    fun `GIVEN processed guess is full WHEN onNextGuess THEN reset current Guess`() = runTest {
        coEvery { guessProcessor.randomWord() } returns world
        sut.onNewGameClick()
        for (i in 0 until 5) {
            sut.onKeyTileClick(KeyTile(key = "A"))
        }
        assertTrue(sut.state.value is GameState.InProgress)
        val fullGuess = Guess(world.map { it.toString() })
        val resultProgress = GameState.InProgress(guess = fullGuess)
        coEvery { guessProcessor.onNextGuess(sut.state.value as GameState.InProgress) } returns resultProgress

        sut.onNextGuess()

        assertTrue(sut.state.value is GameState.InProgress)
        assertEquals(Guess(), (sut.state.value as GameState.InProgress).guess)
    }

    @Test
    fun `GIVEN processed guess is not full WHEN onNextGuess THEN proceed with current guess`() = runTest {
        coEvery { guessProcessor.randomWord() } returns world
        sut.onNewGameClick()
        assertTrue(sut.state.value is GameState.InProgress)
        val partialGuess = Guess(listOf("n", "o"))
        val resultProgress = GameState.InProgress(guess = partialGuess)
        coEvery { guessProcessor.onNextGuess(sut.state.value as GameState.InProgress) } returns resultProgress

        sut.onNextGuess()

        assertTrue(sut.state.value is GameState.InProgress)
        assertEquals(resultProgress, sut.state.value as GameState.InProgress)
        assertEquals(partialGuess, (sut.state.value as GameState.InProgress).guess)
    }
}