package com.tomaszrykala.wordduel.game.board

import java.lang.IllegalArgumentException

data class BoardRow(
    val tile0: Tile, val tile1: Tile, val tile2: Tile, val tile3: Tile, val tile4: Tile
) {
    val tiles: List<Tile> = listOf(tile0, tile1, tile2, tile3, tile4)
    val isGuessed: Boolean = tiles.all { it is Tile.Hit }
    val isActive: Boolean = tiles.all { it is Tile.Active }
    val tilesAsWord: String = tiles.joinToString(separator = "") { it.letter() }
}

fun emptyActiveBoardRow() = BoardRow(
    Tile.Active(), Tile.Active(), Tile.Active(), Tile.Active(), Tile.Active()
)

fun emptyInactiveBoardRow() = BoardRow(
    Tile.Inactive, Tile.Inactive, Tile.Inactive, Tile.Inactive, Tile.Inactive
)

fun boardRowFromString(word: String, isActive: Boolean = false): BoardRow {
    if (word.length != 5) {
        throw IllegalArgumentException("The word must be 5-chars long.")
    } else {
        return if (isActive) {
            BoardRow(
                tile0 = Tile.Active(word[0]),
                tile1 = Tile.Active(word[1]),
                tile2 = Tile.Active(word[2]),
                tile3 = Tile.Active(word[3]),
                tile4 = Tile.Active(word[4]),
            )
        } else {
            BoardRow(
                tile0 = Tile.Hit(word[0]),
                tile1 = Tile.Hit(word[1]),
                tile2 = Tile.Hit(word[2]),
                tile3 = Tile.Hit(word[3]),
                tile4 = Tile.Hit(word[4]),
            )
        }
    }
}
