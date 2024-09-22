package com.tomaszrykala.wordduel.game.board

data class BoardRow(
    val tile0: Tile, val tile1: Tile, val tile2: Tile, val tile3: Tile, val tile4: Tile
) {

    val tiles: List<Tile> = listOf(tile0, tile1, tile2, tile3, tile4)
    val isGuessed: Boolean = tiles.all { it is Tile.Hit }
    val isActive: Boolean = tiles.all { it is Tile.Active }
    val isEmpty: Boolean = tiles.all { it.letter().isEmpty() }
    val isInactive: Boolean = tiles.all { it is Tile.Inactive }
    val tilesAsWord = tiles.joinToString { it.letter() }
}