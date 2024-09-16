package com.tomaszrykala.wordduel

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ShapeDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomaszrykala.wordduel.game.board.Tile
import com.tomaszrykala.wordduel.ui.theme.WordDuelTheme

@Composable
fun BoardTile(
    tile: Tile, nonWordEntered: Boolean = false
) {

    var isAnimated by remember { mutableStateOf(false) }
    isAnimated = nonWordEntered && tile is Tile.Active

    val cellColour = cellColour(tile)
    val color = remember { Animatable(cellColour) }
    val cardColors = CardDefaults.cardColors()

    LaunchedEffect(isAnimated) {
        color.animateTo(Color.Red, animationSpec = tween(400))
        color.animateTo(Color.White, animationSpec = tween(400))
    }

    Card(
        modifier = Modifier
            .width(56.dp)
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(),
        shape = ShapeDefaults.ExtraSmall,
        colors = CardColors(
            containerColor = if (isAnimated) color.value else cellColour,
            contentColor = cardColors.contentColor,
            disabledContainerColor = cardColors.disabledContainerColor,
            disabledContentColor = cardColors.disabledContentColor,
        )
    ) {
        Text(
            text = tile.char(),
            fontSize = 24.sp,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
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

@Preview(showBackground = true)
@Composable
fun BoardTilePreview() {
    WordDuelTheme {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                BoardTile(Tile.Active('h'))
                BoardTile(Tile.Hit('e'))
                BoardTile(Tile.Miss('l'))
                BoardTile(Tile.Misplaced('l'))
                BoardTile(Tile.Misplaced('o'))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                BoardTile(Tile.Inactive)
                BoardTile(Tile.Inactive)
                BoardTile(Tile.Inactive)
                BoardTile(Tile.Inactive)
                BoardTile(Tile.Inactive)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                BoardTile(Tile.Active('w'))
                BoardTile(Tile.Active('o'))
                BoardTile(Tile.Hit('r'))
                BoardTile(Tile.Miss('l'))
                BoardTile(Tile.Misplaced('d'))
            }
        }
    }
}