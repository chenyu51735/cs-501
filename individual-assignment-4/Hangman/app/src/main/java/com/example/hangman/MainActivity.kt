package com.example.hangman

import android.os.Bundle
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hangman.ui.theme.HangmanTheme
import kotlin.random.Random
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HangmanTheme {
                Hangman()
            }
        }
    }
}

@Composable
fun Hangman() {
    val context = LocalContext.current
    var word by rememberSaveable { mutableStateOf("") }
    var guessedLetters by rememberSaveable { mutableStateOf(setOf<Char>()) }
    var incorrectGuesses by rememberSaveable { mutableStateOf(0) }
    var hintCount by rememberSaveable { mutableStateOf(0) }
    var showHintNotAvailable by remember { mutableStateOf(false) }
    var hintMessage by rememberSaveable { mutableStateOf("") }
    var gameOver by rememberSaveable { mutableStateOf(false) }
    var gameWon by rememberSaveable { mutableStateOf(false) }

    val words = listOf("APPLE", "BANANA", "ORANGE")
    val maxIncorrectGuesses = 6

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (showHintNotAvailable) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Hint not available", Toast.LENGTH_SHORT).show()
            showHintNotAvailable = false
        }
    }
    fun startNewGame() {
        word = words[Random.nextInt(words.size)]
        guessedLetters = setOf()
        incorrectGuesses = 0
        hintCount = 0
        gameOver = false
        gameWon = false
    }

    fun onLetterSelected(letter: Char) {
        if (gameOver || letter in guessedLetters) return
        guessedLetters = guessedLetters + letter
        if (letter !in word) {
            incorrectGuesses++
            if (incorrectGuesses >= maxIncorrectGuesses) {
                gameOver = true
            }
        } else if (word.all { it in guessedLetters }) {
            gameWon = true
            gameOver = true
        }
    }

    fun onHintClicked() {
        when (hintCount) {
            0 -> {
                hintMessage = "Fruit"
                hintCount++
            }
            1 -> {
                val remainingIncorrectLetters = ('A'..'Z')
                    .filter { it !in word && it !in guessedLetters }
                val lettersToDisable = remainingIncorrectLetters.take(remainingIncorrectLetters.size / 2)
                guessedLetters = guessedLetters + lettersToDisable
                incorrectGuesses++
                hintCount++
                if (incorrectGuesses >= maxIncorrectGuesses) {
                    gameOver = true
                }
            }
            2 -> {
                val vowels = listOf('A', 'E', 'I', 'O', 'U')
                guessedLetters = guessedLetters + vowels
                incorrectGuesses++
                hintCount++
                if (incorrectGuesses >= maxIncorrectGuesses) {
                    gameOver = true
                }
            }
        }
    }

    if (word.isEmpty()) {
        startNewGame()
    }

    // Main UI
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { startNewGame() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("New Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Choose a Letter", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LetterButtons(
                        guessedLetters = guessedLetters,
                        onLetterSelected = ::onLetterSelected,
                        enabled = !gameOver
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = {
                            // Only cost a turn on the 2nd or 3rd hint
                            val costsTurn = (hintCount == 1 || hintCount == 2)
                            // If using this hint would cause a loss, show toast
                            if (costsTurn && incorrectGuesses == maxIncorrectGuesses - 1) {
                                showHintNotAvailable = true
                            } else if (!gameOver && hintCount < 3) {
                                onHintClicked()
                            }
                        },
                        enabled = (!gameOver && hintCount < 3)
                    ) {
                        Text("Hint")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    if (hintMessage.isNotEmpty()) {
                        Text(hintMessage)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    HangmanFigure(incorrectGuesses = incorrectGuesses)
                    Spacer(modifier = Modifier.height(16.dp))
                    WordDisplay(word = word, guessedLetters = guessedLetters)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (gameOver) {
                        Text(
                            text = if (gameWon) "You Won!" else "You Lost! The word was $word",
                            fontSize = 24.sp
                        )
                    }
                }
            }
        } else {
            // Portrait Mode: Simplified Layout
            Column(modifier = Modifier.fillMaxSize()) {
                Text("Choose a Letter", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LetterButtons(
                    guessedLetters = guessedLetters,
                    onLetterSelected = ::onLetterSelected,
                    enabled = !gameOver
                )

                Spacer(modifier = Modifier.height(16.dp))

                HangmanFigure(incorrectGuesses = incorrectGuesses)
                Spacer(modifier = Modifier.height(16.dp))
                WordDisplay(word = word, guessedLetters = guessedLetters)
                Spacer(modifier = Modifier.height(16.dp))
                if (gameOver) {
                    Text(
                        text = if (gameWon) "You Won!" else "You Lost! The word was $word",
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LetterButtons(
    guessedLetters: Set<Char>,
    onLetterSelected: (Char) -> Unit,
    enabled: Boolean
) {
    val letters = ('A'..'Z').toList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        letters.chunked(6).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { letter ->
                    Button(
                        onClick = { onLetterSelected(letter) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        enabled = enabled && letter !in guessedLetters
                    ) {
                        Text(letter.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun HangmanFigure(incorrectGuesses: Int) {
    // Draw the hangman figure step by step
    Canvas(modifier = Modifier.size(200.dp)) {
        val width = size.width
        val height = size.height

        drawLine(
            color = Color.Black,
            start = Offset(x = width * 0.2f, y = height * 0.8f),
            end = Offset(x = width * 0.8f, y = height * 0.8f),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(x = width * 0.5f, y = height * 0.1f),
            end = Offset(x = width * 0.5f, y = height * 0.8f),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(x = width * 0.5f, y = height * 0.1f),
            end = Offset(x = width * 0.2f, y = height * 0.1f),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(x = width * 0.2f, y = height * 0.1f),
            end = Offset(x = width * 0.2f, y = height * 0.2f),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )

        // Draw the hangman based on incorrect guesses
        if (incorrectGuesses >= 1) {
            drawCircle(
                color = Color.Black,
                center = Offset(x = width * 0.2f, y = height * 0.3f),
                radius = 20f,
                style = Stroke(width = 4f)
            )
        }
        if (incorrectGuesses >= 2) {
            drawLine(
                color = Color.Black,
                start = Offset(x = width * 0.2f, y = height * 0.3f + 20f),
                end = Offset(x = width * 0.2f, y = height * 0.6f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        if (incorrectGuesses >= 3) {
            drawLine(
                color = Color.Black,
                start = Offset(x = width * 0.2f, y = height * 0.4f),
                end = Offset(x = width * 0.1f, y = height * 0.5f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        if (incorrectGuesses >= 4) {
            drawLine(
                color = Color.Black,
                start = Offset(x = width * 0.2f, y = height * 0.4f),
                end = Offset(x = width * 0.3f, y = height * 0.5f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        if (incorrectGuesses >= 5) {
            drawLine(
                color = Color.Black,
                start = Offset(x = width * 0.2f, y = height * 0.6f),
                end = Offset(x = width * 0.1f, y = height * 0.8f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        if (incorrectGuesses >= 6) {
            drawLine(
                color = Color.Black,
                start = Offset(x = width * 0.2f, y = height * 0.6f),
                end = Offset(x = width * 0.3f, y = height * 0.8f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun WordDisplay(word: String, guessedLetters: Set<Char>) {
    val displayWord = word.map { if (it in guessedLetters) it else '_' }.joinToString(" ")
    Text(
        text = displayWord,
        fontSize = 24.sp
    )
}