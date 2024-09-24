package com.tomaszrykala.wordduel.game.keyboard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class KeyTile(val key: String, val color: Int = COLOR_DEFAULT)

fun createKeyTiles(): List<List<KeyTile>> {
    val top = keyboardTopRow.map { KeyTile(it) }
    val mid = keyboardMidRow.map { KeyTile(it) }
    val bottom = keyboardBottomRow.map { KeyTile(it) }

    return listOf(top, mid, bottom)
}

const val KEY_DEL = "[DEL]"
val COLOR_DEFAULT = Color.LightGray.toArgb()

private val keyboardTopRow = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
private val keyboardMidRow = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
private val keyboardBottomRow = listOf("Z", "X", "C", "V", "B", "N", "M", KEY_DEL)
