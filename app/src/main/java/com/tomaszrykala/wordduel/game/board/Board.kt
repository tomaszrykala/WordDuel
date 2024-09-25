package com.tomaszrykala.wordduel.game.board

data class Board(val boardRows: List<BoardRow> = startGameBoard()) {

    val isFull: Boolean = boardRows.none { it.isActive }
    internal val isGuessed: Boolean = boardRows.any { it.isGuessed }
    internal val isEnded: Boolean = isFull || isGuessed
    val attemptCount: Int = boardRows.indexOfFirst { it.isGuessed } + 1 // it's zero-based
}

private fun startGameBoard() = listOf(
    emptyActiveBoardRow(),
    emptyInactiveBoardRow(),
    emptyInactiveBoardRow(),
    emptyInactiveBoardRow(),
    emptyInactiveBoardRow(),
    emptyInactiveBoardRow(),
)
