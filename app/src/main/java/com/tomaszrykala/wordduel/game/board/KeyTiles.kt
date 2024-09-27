package com.tomaszrykala.wordduel.game.board

import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.keyboard.createKeyTiles

data class KeyTiles(val allTiles: List<List<KeyTile>> = createKeyTiles()) {
    val top get() = allTiles[0]
    val mid get() = allTiles[1]
    val bottom get() = allTiles[2]
}