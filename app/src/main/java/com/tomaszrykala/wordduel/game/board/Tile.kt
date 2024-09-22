package com.tomaszrykala.wordduel.game.board

sealed class Tile(private val char: Char?) {
    data object Inactive : Tile(null)
    data class Active(private val _char: Char? = null) : Tile(_char)
    data class Hit(private val _char: Char?) : Tile(_char)
    data class Miss(private val _char: Char?) : Tile(_char)
    data class Misplaced(private val _char: Char?) : Tile(_char)

    fun letter(): String = char?.uppercase() ?: ""
}