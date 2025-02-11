package com.example.typingspeedtest

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
import com.example.typingspeedtest.ui.theme.TypingSpeedTestTheme
import org.xmlpull.v1.XmlPullParser
import android.content.Context
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment

data class WordItem(val word: String, val addedTime: Long)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TypingSpeedTestTheme {
                TypingSpeedTest()
                }
            }
        }
    }

fun loadTypingWords(context: Context): List<String> {
    val words = mutableListOf<String>()
    val parser: XmlPullParser = context.resources.getXml(R.xml.typingwords)
    var eventType = parser.eventType

    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && parser.name == "word") {
            val wordText = parser.nextText().trim()
            if (wordText.isNotEmpty()) {
                words.add(wordText)
            }
        }
        eventType = parser.next()
    }
    return words
}

@Composable
fun TypingSpeedTest() {
    val context = LocalContext.current
    val wordPool = remember { loadTypingWords(context) }
    val displayedWords = remember { mutableStateListOf<WordItem>() }
    var inputText by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        repeat(5) {
            displayedWords.add(WordItem(wordPool.random(), System.currentTimeMillis()))
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            val currentTime = System.currentTimeMillis()
            val expiredWords = displayedWords.filter { currentTime - it.addedTime >= 5000 }
            expiredWords.forEach { expired ->
                displayedWords.remove(expired)
                displayedWords.add(WordItem(wordPool.random(), System.currentTimeMillis()))
            }
        }
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                items(displayedWords) { wordItem ->
                    Text(
                        text = wordItem.word,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        TextField(
            value = inputText,
            onValueChange = { newText ->
                inputText = newText
                val trimmedInput = newText.trim()
                val matchedItem = displayedWords.find {
                    it.word.equals(trimmedInput, ignoreCase = true)
                }
                if (matchedItem != null) {
                    displayedWords.remove(matchedItem)
                    displayedWords.add(WordItem(wordPool.random(), System.currentTimeMillis()))
                    inputText = ""
                }
            },
            label = { Text("Type here") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}