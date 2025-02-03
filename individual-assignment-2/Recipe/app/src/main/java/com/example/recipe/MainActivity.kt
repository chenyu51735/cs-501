package com.example.recipe

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
import com.example.recipe.ui.theme.RecipeTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeTheme {
                Recipe()
            }
        }
    }
}

@Composable
fun Recipe(){
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Cheese Stuffed Bagel",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontSize = 24.sp),
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bagel),
                    contentDescription = "bagel",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Text(
                text = "Ingredients:",
                style = TextStyle(fontSize = 20.sp),
                fontStyle = FontStyle.Italic
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                val ingredients = listOf(
                    "1 bagel",
                    "1 ounce cream cheese, cut into 8 slices",
                    "1 tablespoon hot honey or regular honey, plus more for drizzle",
                    "1 tablespoon butter",
                )

                ingredients.forEach { ingredient ->
                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp))
                        Text(text = ingredient)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Text(
                text = "Instructions:",
                style = TextStyle(fontSize = 20.sp),
                fontStyle = FontStyle.Italic
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                val instructions = listOf(
                    "1. Preheat an air fryer to 390 degrees F (199 degrees C) according to manufacturer's instructions.",
                    "2. Make 8 small cuts in the bagel, taking care not to cut all the way through, to create small openings.  Add a piece of cream cheese in each crevice around the bagel. Combine honey and melted butter in a small dish and coat the stuffed bagel liberally. Sprinkle with salt.",
                    "3. Place bagel in the air fryer until golden brown, 5 to 7 minutes. Drizzle with more honey if desired.",
                )

                instructions.forEach { instruction ->
                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp))
                        Text(text = instruction)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}