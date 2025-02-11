package com.example.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sudoku()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sudoku() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var board by remember { mutableStateOf(resetBoard()) }

    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var inputValue by remember { mutableStateOf("") }

    LaunchedEffect(board) {
        if (checkSudoku(board)) {
            snackbarHostState.showSnackbar("You won!")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Sudoku") },
                actions = {
                    Button(onClick = { board = resetBoard() }) {
                        Text("Reset")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(9),
                    modifier = Modifier.weight(1f)
                ) {
                    items(81) { index ->
                        val row = index / 9
                        val col = index % 9
                        val cellValue = board[row][col]
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .background(
                                    if (row == 0 && cellValue != null) Color.LightGray else Color.White
                                )
                                .border(1.dp, Color.Black)
                                .clickable(enabled = (cellValue == null)) {
                                    selectedCell = Pair(row, col)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (cellValue != null) {
                                Text(text = cellValue.toString(), fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    )

    if (selectedCell != null) {
        AlertDialog(
            onDismissRequest = {
                selectedCell = null
                inputValue = ""
            },
            title = { Text("Enter number (1-9)") },
            text = {
                TextField(
                    value = inputValue,
                    onValueChange = { newVal ->
                        if (newVal.length <= 1 && (newVal.isEmpty() || newVal.toIntOrNull() in 1..9)) {
                            inputValue = newVal
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = {
                    val num = inputValue.toIntOrNull()
                    if (num != null && num in 1..9 && selectedCell != null) {
                        val (r, c) = selectedCell!!
                        if (isValidPlacement(board, r, c, num)) {
                            board = board.mapIndexed { rowIndex, row ->
                                if (rowIndex == r) {
                                    row.mapIndexed { colIndex, cell ->
                                        if (colIndex == c) num else cell
                                    }
                                } else row
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Duplicates found")
                            }
                        }
                    }
                    selectedCell = null
                    inputValue = ""
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = {
                    selectedCell = null
                    inputValue = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun resetBoard(): List<List<Int?>> {
    val firstRow = (1..9).shuffled()
    return List(9) { row ->
        if (row == 0) firstRow else List(9) { null }
    }
}

fun checkSudoku(board: List<List<Int?>>): Boolean {
    // Board must be completely filled.
    if (board.any { row -> row.any { it == null } }) return false

    for (row in board) {
        if (row.toSet() != (1..9).toSet()) return false
    }

    for (col in 0 until 9) {
        val column = board.map { it[col]!! }.toSet()
        if (column != (1..9).toSet()) return false
    }

    for (blockRow in 0 until 3) {
        for (blockCol in 0 until 3) {
            val block = mutableListOf<Int>()
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    block.add(board[blockRow * 3 + i][blockCol * 3 + j]!!)
                }
            }
            if (block.toSet() != (1..9).toSet()) return false
        }
    }

    return true
}

fun isValidPlacement(board: List<List<Int?>>, row: Int, col: Int, num: Int): Boolean {
    if (board[row].contains(num)) return false
    for (i in 0 until 9) {
        if (board[i][col] == num) return false
    }
    val startRow = (row / 3) * 3
    val startCol = (col / 3) * 3
    for (i in startRow until startRow + 3) {
        for (j in startCol until startCol + 3) {
            if (board[i][j] == num) return false
        }
    }
    return true
}