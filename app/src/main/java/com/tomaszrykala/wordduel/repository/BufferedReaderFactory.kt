package com.tomaszrykala.wordduel.repository

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import javax.inject.Inject

class BufferedReaderFactory @Inject constructor() {

    fun bufferedReader(inputStream: InputStream) = BufferedReader(
        InputStreamReader(inputStream, Charset.forName(CHAR_SET))
    )

    suspend fun processLines(reader: BufferedReader): List<String> = reader.readLines()

    private companion object {
        const val CHAR_SET = "UTF-8"
    }
}