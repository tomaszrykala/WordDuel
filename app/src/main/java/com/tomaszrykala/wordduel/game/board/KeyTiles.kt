package com.tomaszrykala.wordduel.game.board

import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.keyboard.createKeyTiles

data class KeyTiles(val keyTiles: List<List<KeyTile>> = createKeyTiles()) {
    val top get() = keyTiles[0]
    val mid get() = keyTiles[1]
    val bottom get() = keyTiles[2]
}