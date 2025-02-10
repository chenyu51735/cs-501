package com.example.flashcard

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
import com.example.flashcard.ui.theme.FlashcardTheme
import android.content.Context
import org.xmlpull.v1.XmlPullParser
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.material3.CardDefaults

data class Flashcard(val question: String, val answer: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardTheme {
                Flashcards()
                }
            }
        }
    }
@Composable
fun Flashcards() {
    val context = LocalContext.current
    val initialFlashcards = remember { parseFlashcards(context) }
    val flashcardsList = remember { mutableStateListOf<Flashcard>().apply { addAll(initialFlashcards) } }

    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000)
            flashcardsList.shuffle()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(flashcardsList) { flashcard ->
                FlashcardItem(flashcard = flashcard)
            }
        }
    }
}
fun parseFlashcards(context: Context): List<Flashcard> {
    val flashcards = mutableListOf<Flashcard>()
    val parser: XmlPullParser = context.resources.getXml(R.xml.flashcards)
    var eventType = parser.eventType

    var currentQuestion: String? = null
    var currentAnswer: String? = null

    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "question" -> {
                        currentQuestion = parser.nextText()
                    }
                    "answer" -> {
                        currentAnswer = parser.nextText()
                    }
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "flashcard") {
                    if (currentQuestion != null && currentAnswer != null) {
                        flashcards.add(Flashcard(currentQuestion, currentAnswer))
                    }
                    currentQuestion = null
                    currentAnswer = null
                }
            }
        }
        eventType = parser.next()
    }
    return flashcards
}
@Composable
fun FlashcardItem(flashcard: Flashcard) {
    var flipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (flipped) 180f else 0f)

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
            .clipToBounds()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clickable { flipped = !flipped },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            Card(
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = flashcard.question, fontSize = 20.sp)
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = flashcard.answer, fontSize = 20.sp)
                }
            }
        }
    }
}