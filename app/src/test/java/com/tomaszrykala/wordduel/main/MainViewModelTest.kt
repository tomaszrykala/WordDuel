package com.tomaszrykala.wordduel.main

import com.tomaszrykala.wordduel.game.processor.GuessProcessor
import io.mockk.mockk

class MainViewModelTest {

    private val guessProcessor: GuessProcessor = mockk()

    private val sut = MainViewModel(guessProcessor)

}