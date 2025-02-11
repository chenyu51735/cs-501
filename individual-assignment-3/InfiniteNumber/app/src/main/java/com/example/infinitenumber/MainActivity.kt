package com.example.infinitenumber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.infinitenumber.ui.theme.InfiniteNumberTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InfiniteNumberTheme {
                InfiniteNumber()
            }
        }
    }
}
@Composable
fun InfiniteNumber() {
    val numbers = remember { mutableStateListOf<Int>() }
    LaunchedEffect(Unit) {
        numbers.addAll((1..30).toList())
    }
    val gridState = rememberLazyGridState()
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .map { it ?: 0 }
            .distinctUntilChanged()
            .filter { lastVisibleIndex -> lastVisibleIndex >= numbers.size - 1 }
            .collectLatest {
                delay(1000)
                val nextStart = numbers.size + 1
                val nextEnd = nextStart + 29
                numbers.addAll((nextStart..nextEnd).toList())
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                numbers.clear()
                numbers.addAll((1..30).toList())
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Reset")
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = gridState,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(numbers) { number ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .aspectRatio(1f)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = number.toString(), fontSize = 20.sp, color = Color.Black)
                }
            }
        }
    }
}