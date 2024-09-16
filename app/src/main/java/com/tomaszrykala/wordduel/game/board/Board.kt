package com.tomaszrykala.wordduel.game.board

import java.lang.IllegalArgumentException

data class Board(val boardRows: List<BoardRow> = startGameBoard()) {

    val isFull: Boolean = boardRows.none { it.isActive }
    val isGuessed: Boolean = boardRows.any { it.isGuessed }
    val isEnded: Boolean = isFull || isGuessed
    val indexOfGuessed: Int = boardRows.indexOfFirst { it.isGuessed }
    val isEmpty: Boolean = with(boardRows[0]) { this.isActive && this.isEmpty }
}

private fun startGameBoard() = listOf(
    emptyActiveBoardRow(),
    emptyInactiveBoardRow(),
    emptyInactiveBoardRow(),
    emptyInactiveBoardRow(),
    emptyInactiveBoardRow(),
    emptyInactiveBoardRow(),
)

fun emptyActiveBoardRow() = BoardRow(
    Tile.Active(), Tile.Active(), Tile.Active(), Tile.Active(), Tile.Active()
)

fun emptyInactiveBoardRow() = BoardRow(
    Tile.Inactive, Tile.Inactive, Tile.Inactive, Tile.Inactive, Tile.Inactive
)

fun boardRowFromString(word: String): BoardRow {
    if (word.length != 5) {
        throw IllegalArgumentException("The word must be 5-chars long.")
    } else {
        return BoardRow(
            tile0 = Tile.Hit(word[0]),
            tile1 = Tile.Hit(word[1]),
            tile2 = Tile.Hit(word[2]),
            tile3 = Tile.Hit(word[3]),
            tile4 = Tile.Hit(word[4]),
        )
    }
}
