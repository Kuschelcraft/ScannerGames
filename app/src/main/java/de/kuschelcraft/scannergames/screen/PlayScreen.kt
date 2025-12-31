package de.kuschelcraft.scannergames.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import de.kuschelcraft.scannergames.viewmodel.LeaderboardViewModel

enum class GameOverReason {
    TIME_UP,
    DUPLICATE_SCAN,
    NONE
}

@Composable
fun PlayScreen(viewModel: LeaderboardViewModel) {
    PlayScreenContent(
        onScanBarcode = {
            return@PlayScreenContent
        },
        viewModel = viewModel
    )
}


@Composable
fun PlayScreenContent(
    onScanBarcode: (String) -> Unit,
    viewModel: LeaderboardViewModel
) {
    var inputText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var isPlaying by remember { mutableStateOf(false) }
    var timeLeft by remember { androidx.compose.runtime.mutableLongStateOf(300L) } // 300 seconds = 5 minutes
    var score by remember { androidx.compose.runtime.mutableIntStateOf(0) } // Dynamic score
    val scannedCodes = remember { mutableListOf<String>() } // List to store scanned codes

    var showGameOverDialog by remember { mutableStateOf(false) }
    var gameOverReason by remember { mutableStateOf(GameOverReason.NONE) }

    fun stopGame(reason: GameOverReason) {
        isPlaying = false
        gameOverReason = reason
        showGameOverDialog = true
    }

    fun processScan() {
        if (inputText.isNotBlank() && isPlaying) {
            if (scannedCodes.contains(inputText)) {
                stopGame(GameOverReason.DUPLICATE_SCAN)
            } else {
                scannedCodes.add(inputText)
                onScanBarcode(inputText)
                score += 1
            }
            inputText = ""
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            // Reset keyboard/focus when game starts
            keyboardController?.hide()
            focusRequester.requestFocus()

            while (timeLeft > 0) {
                kotlinx.coroutines.delay(1000L)
                timeLeft--
            }
            stopGame(GameOverReason.TIME_UP)
        }
    }

    LaunchedEffect(Unit) {
        keyboardController?.hide()
        focusRequester.requestFocus()
    }

    if (showGameOverDialog) {
        GameOverDialog(
            score = score,
            reason = gameOverReason,
            onDismiss = {
                showGameOverDialog = false
            },
            onSave = { name ->
                viewModel.addItem(name = name, score)
                showGameOverDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = inputText,
            enabled = isPlaying,
            onValueChange = { inputText = it },
            placeholder = { Text("Enter or scan barcode") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                        processScan()
                        true
                    } else false
                },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                processScan()
            })
        )
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .weight(1f) // Fill remaining space
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = androidx.compose.ui.Alignment.BottomCenter
        ) {
            PlayInfos(timeLeftSeconds = timeLeft, score = score)
            androidx.compose.animation.AnimatedVisibility(
                visible = !isPlaying,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
            ) {
                androidx.compose.material3.ExtendedFloatingActionButton(
                    onClick = {
                        isPlaying = true
                        timeLeft = 300L // Reset to 5 mins
                        score = 0
                        scannedCodes.clear()
                    },
                    icon = {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                    },
                    text = { Text("START GAME") }
                )
            }
        }
    }
}

@Composable
fun GameOverDialog(
    score: Int,
    reason: GameOverReason,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (reason == GameOverReason.TIME_UP) "Time's up!" else "Game Over!")
        },
        text = {
            Column {
                Text(
                    text = if (reason == GameOverReason.DUPLICATE_SCAN)
                        "You scanned a code twice! Final Score: $score"
                    else
                        "You ran out of time! Final Score: $score"
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Enter Name for Leaderboard") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Save Score")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Discard")
            }
        }
    )
}

@Composable
fun PlayInfos(
    timeLeftSeconds: Long,
    score: Int
) {
    val formattedTime = remember(timeLeftSeconds) {
        val minutes = timeLeftSeconds / 60
        val seconds = timeLeftSeconds % 60
        "%d:%02d".format(minutes, seconds)
    }

    Column(        modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        // Obere Zahl (z.B. aktueller Punktestand)
        Text(
            text = formattedTime, // Platzhalter-Wert
            style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
            color = if (timeLeftSeconds < 30) androidx.compose.material3.MaterialTheme.colorScheme.error else androidx.compose.material3.MaterialTheme.colorScheme.secondary
        )

        // Kleines Label dazwischen (optional, zur Erklärung)
        Text(
            text = "TIME",
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge
        )

        // Etwas Abstand
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))

        // Untere Zahl (z.B. verbleibende Zeit oder Level)
        Text(
            text = "$score", // Platzhalter-Wert
            style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )

        // Kleines Label darunter (optional)
        Text(
            text = "POINTS",
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge
        )
    }
}