package com.tomaszrykala.wordduel.game.main

import androidx.lifecycle.ViewModel
import com.tomaszrykala.wordduel.game.keyboard.KEY_DEL
import com.tomaszrykala.wordduel.game.state.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<GameState>(GameState())
    val state: StateFlow<GameState> = _state

    fun onCurrentGuessChange(letter: String) {
        val currentGuess = _state.value.currentGuess
        if (KEY_DEL == letter && currentGuess.size > 0) {
            val subList = currentGuess.subList(0, currentGuess.size - 1)
            _state.value = _state.value.copy(currentGuess = subList)
        } else {
            currentGuess.add(letter)
            _state.value = _state.value.copy(currentGuess = currentGuess)
        }
        if (currentGuess.size == 5) {
            currentGuess.clear()
            _state.value = _state.value.copy(currentGuess = currentGuess)
        }
    }
}