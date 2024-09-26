package com.tomaszrykala.wordduel.repository

import android.content.Context
import android.content.res.Resources
import com.tomaszrykala.wordduel.MainDispatcherRule
import com.tomaszrykala.wordduel.repository.WordRepository.Companion.ALL_WORDS
import com.tomaszrykala.wordduel.repository.WordRepository.Companion.RE_INIT_ATTEMPT_ERROR
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStream

class WordRepositoryTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private val readerFactory: BufferedReaderFactory = mockk()

    private val sut = WordRepository(readerFactory, rule.testDispatcher)

    @Test
    fun `GIVEN Success is produced WHEN initDictionary THEN return Success`() = runTest {
        val result: Result<Unit> = initDictionary()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `GIVEN Error is producedWHEN initDictionary THEN return Error`() = runTest {
        val error = "BOO"
        val inputStream = mockk<InputStream>()
        val resources = mockk<Resources> { every { openRawResource(ALL_WORDS) } returns inputStream }
        val context = mockk<Context> { every { this@mockk.resources } returns resources }
        every { readerFactory.bufferedReader(inputStream) } throws IllegalStateException(error)

        val resultTwo = sut.initDictionary(context)

        assertTrue(resultTwo.isFailure)
        assertEquals(error, resultTwo.exceptionOrNull()!!.message)
    }

    @Test
    fun `GIVEN Dictionary is already init WHEN initDictionary THEN return Error`() = runTest {
        val resultOne: Result<Unit> = initDictionary()
        assertTrue(resultOne.isSuccess)

        val resultTwo: Result<Unit> = initDictionary()

        assertTrue(resultTwo.isFailure)
        assertEquals(RE_INIT_ATTEMPT_ERROR, resultTwo.exceptionOrNull()!!.message)
    }

    @Test
    fun `GIVEN dictionary is empty WHEN randomWord THEN throws Exception`() {
        val exceptions = mutableListOf<Exception>()
        try {
            sut.randomWord()
        } catch (e: NoSuchElementException) {
            exceptions.add(e)
        } finally {
            assertTrue(exceptions.isNotEmpty())
        }
    }

    @Test
    fun `WHEN randomWord THEN return a random word`() = runTest {
        val resultOne: Result<Unit> = initDictionary()
        assertTrue(resultOne.isSuccess)

        val result = sut.randomWord()

        assertNotNull(result)
        assertTrue(listOf("HELLO", world).contains(result))
    }

    @Test
    fun `GIVEN word exists WHEN searchWord THEN return True`() = runTest {
        val resultOne: Result<Unit> = initDictionary()
        assertTrue(resultOne.isSuccess)

        val result = sut.searchWord(world)

        assertTrue(result)
    }

    @Test
    fun `GIVEN word does not exist WHEN searchWord THEN return False`() = runTest {
        val resultOne: Result<Unit> = initDictionary()
        assertTrue(resultOne.isSuccess)

        val result = sut.searchWord("EMPTY")

        assertFalse(result)
    }

    @Test
    fun `GIVEN dictionary is empty WHEN searchWord THEN return False`() {
        val result = sut.searchWord(world)

        assertFalse(result)
    }

    private suspend fun initDictionary(): Result<Unit> {
        val reader = mockk<BufferedReader>(relaxUnitFun = true)
        val inputStream = mockk<InputStream>(relaxUnitFun = true)
        val resources = mockk<Resources> { every { openRawResource(ALL_WORDS) } returns inputStream }
        val context = mockk<Context> { every { this@mockk.resources } returns resources }
        every { readerFactory.bufferedReader(inputStream) } returns reader
        every { readerFactory.processLines(reader) } returns dictionary

        return sut.initDictionary(context)
    }

    private companion object {
        private const val world = "WORLD"
        private val dictionary = listOf("HELLO", world)
    }
}