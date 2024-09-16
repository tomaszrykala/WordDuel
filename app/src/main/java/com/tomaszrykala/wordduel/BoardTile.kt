package com.tomaszrykala.wordduel

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomaszrykala.wordduel.game.board.Tile

@Composable
fun BoardTile(tile: Tile, nonWordEntered: Boolean) {

    var isAnimated by remember { mutableStateOf(false) }
    isAnimated = nonWordEntered && tile is Tile.Active

    val cellColour = cellColour(tile)
    val color = remember { Animatable(cellColour) }

    LaunchedEffect(isAnimated) {
        color.animateTo(Color.Red, animationSpec = tween(400))
        color.animateTo(Color.White, animationSpec = tween(400))
    }

    Card(
        modifier = Modifier
            .width(56.dp)
            .background(if (isAnimated) color.value else cellColour)
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(),
    ) {
        Text(
            text = tile.char(),
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp),
            color = textColour(tile),
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
    }
}

private fun textColour(tile: Tile) =
    if (tile is Tile.Inactive) Color.LightGray else Color.DarkGray

private fun cellColour(tile: Tile): Color = when (tile) {
    is Tile.Hit -> Color.Green
    is Tile.Miss -> Color.Gray
    is Tile.Misplaced -> Color.Yellow
    is Tile.Active -> Color.White
    Tile.Inactive -> Color.Black
}