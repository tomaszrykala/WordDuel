package com.tomaszrykala.wordduel.main.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomaszrykala.wordduel.game.keyboard.KEY_DEL
import com.tomaszrykala.wordduel.game.keyboard.KeyTile
import com.tomaszrykala.wordduel.game.state.KeyTiles

@Composable
fun SoftKeyboard(
    keyTiles: KeyTiles,
    onKeyTileClick: (k: KeyTile) -> Unit
) {

    val listOfTiles = listOf(keyTiles.top, keyTiles.mid, keyTiles.bottom)

    listOfTiles.forEach { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.Center
        ) {

            row.forEach { keyTileValue ->

                Button(
                    modifier = Modifier
                        .height(52.dp)
                        .padding(2.dp)
                        .width(if (keyTileValue.key == KEY_DEL) 46.dp else 28.dp)
                        .align(Alignment.CenterVertically),
                    elevation = ButtonDefaults.elevatedButtonElevation(),
                    shape = ShapeDefaults.ExtraSmall,
                    contentPadding = PaddingValues(2.dp),
                    colors = ButtonDefaults.textButtonColors(containerColor = Color(keyTileValue.color)),
                    onClick = { onKeyTileClick(keyTileValue) },
                ) {
                    Text(
                        text = keyTileValue.key,
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
