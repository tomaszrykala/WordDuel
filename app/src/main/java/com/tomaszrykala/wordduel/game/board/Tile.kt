package com.tomaszrykala.wordduel.game.board

sealed class Tile(private val letter: Char?) {
    data object Inactive : Tile(null)
    data class Active(private val _letter: Char? = null) : Tile(_letter)
    data class Hit(private val _letter: Char?) : Tile(_letter)
    data class Miss(private val _letter: Char?) : Tile(_letter)
    data class Misplaced(private val _letter: Char?) : Tile(_letter)
    fun char(): String = letter?.uppercase() ?: ""
}