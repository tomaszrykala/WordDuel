package com.tomaszrykala.wordduel.game.main.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tomaszrykala.wordduel.ui.theme.WordDuelTheme

@Composable
fun DictionaryLoadingDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Loading dictionary...") },
        text = { Text("Please wait") },
        confirmButton = {}
    )
}

@Composable
fun ErrorDialog(text: String, onRetry: () -> Unit) {

    var showDialog by rememberSaveable { mutableStateOf(true) }
    val onDismissRequest = { showDialog = false }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Error loading!") },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = {
                onDismissRequest.invoke()
                onRetry.invoke()
            }) { Text(stringResource(android.R.string.ok)) }
        }
    )
}

@Composable
fun GameEndedDialog(
    word: String, attempt: Int, isGuessed: Boolean
) {

    var showDialog by rememberSaveable { mutableStateOf(true) }
    val onDismissRequest = { showDialog = false }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                val text = if (isGuessed) "Congratulations!" else "Oops!"
                Text(text)
            },
            text = {
                val text = if (isGuessed) "You guessed in $attempt out of 6 attempts."
                else "The word was: '$word'.\nBetter luck next time."
                Text(text)
            },
            confirmButton = {
                Button(onClick = onDismissRequest) { Text(stringResource(android.R.string.ok)) }
            }
        )
    }
}

@Preview
@Composable
fun DictionaryLoadingDialogPreview() {
    WordDuelTheme {
        DictionaryLoadingDialog()
    }
}

@Preview
@Composable
fun ErrorDialogDialogPreview() {
    WordDuelTheme {
        ErrorDialog("Bad!") {}
    }
}


@Preview
@Composable
fun GameEndedDialogGuessedPreview() {
    WordDuelTheme {
        GameEndedDialog("hello", 4, true)
    }
}

@Preview
@Composable
fun GameEndedDialogNotGuessedPreview() {
    WordDuelTheme {
        GameEndedDialog("hello", 4, false)
    }
}
