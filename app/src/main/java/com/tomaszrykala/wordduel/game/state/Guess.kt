package com.tomaszrykala.wordduel.game.state

data class Guess(val guess: List<String> = listOf()) {
    fun isFull(): Boolean = with(guess) { size == 5 && this.all { it != "" } }
    fun asString(): String = guess.joinToString(separator = "") { it }.lowercase()
}