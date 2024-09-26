package com.tomaszrykala.wordduel.repository

import android.content.Context
import com.tomaszrykala.wordduel.R
import com.tomaszrykala.wordduel.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

class WordRepository @Inject constructor(
    private val readerFactory: BufferedReaderFactory,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    private val dictionary: Trie = Trie()
    private val words: MutableList<String> = mutableListOf()

    suspend fun initDictionary(context: Context): Result<Unit> {
        if (words.isEmpty()) {
            runCatching {
                withContext(ioDispatcher) {
                    val inputStream: InputStream = context.resources.openRawResource(ALL_WORDS)
                    val reader = readerFactory.bufferedReader(inputStream)
                    val readLines = readerFactory.processLines(reader)
                    readLines.forEach {
                        dictionary.insert(it)
                        words.add(it)
                    }
                    reader.close()
                    inputStream.close()
                }
            }.fold(
                onSuccess = { return Result.success(Unit) },
                onFailure = { return Result.failure(it) }
            )
        } else {
            return Result.failure(IllegalStateException(RE_INIT_ATTEMPT_ERROR))
        }
    }

    fun randomWord(): String = words.random()

    fun searchWord(word: String): Boolean = dictionary.search(word)

    internal companion object {
        val ALL_WORDS = R.raw.words
        const val RE_INIT_ATTEMPT_ERROR = "Requested init of an already initialised Repository."
    }
}