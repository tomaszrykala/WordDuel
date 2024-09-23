package com.tomaszrykala.wordduel.repository

import android.content.Context
import com.tomaszrykala.wordduel.R
import com.tomaszrykala.wordduel.game.processor.Trie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

class WordRepository @Inject constructor(private val readerFactory: BufferedReaderFactory) {

    private val dictionary: Trie = Trie() // 1,5MB allocation :|
    private val words: MutableList<String> = mutableListOf() // TODO: Bad for perf, heavy on memory, but 25K alloc?

    suspend fun initDictionary(context: Context): Result<Unit> {
        if (dictionary.isEmpty) { // words.isEmpty()
            runCatching {
                withContext(Dispatchers.IO) {
                    val inputStream: InputStream = context.resources.openRawResource(WORDS_ALL)
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
            return Result.failure(IllegalStateException("Requested init of an already initialised Repository."))
        }
    }

    fun randomWord(): String {
        // return dictionary.random()
        return words.random()
    }

    fun searchWord(word: String): Boolean = dictionary.search(word)

    private companion object {
        val WORDS_ALL = R.raw.five_letter_words
    }
}