package com.example.musicplayer


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicplayer.ui.theme.MusicPlayerTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.Icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                MusicPlayer()
            }
        }
    }
}

@Composable
fun MusicPlayer(){
    Box()
    {
        Player(imageRes = R.drawable.album, caption = "Here With Me")
    }
}
@Composable
fun Player(imageRes: Int, caption: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = caption,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = caption, fontSize = 24.sp)
        Text(
            text = "D4vd",
            fontSize = 18.sp,
            color = Color.DarkGray
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                onClick = {},
            ){
                Icon(
                    painter = painterResource(id = R.drawable.pause_24px),
                    contentDescription = "Pause"
                )
            }
            Button(
                onClick = {},
            ){
                Icon(
                    painter = painterResource(id = R.drawable.play_arrow_24px),
                    contentDescription = "Play"
                )
            }
            Button(
                onClick = {},
            ){
                Icon(
                    painter = painterResource(id = R.drawable.skip_next_24px),
                    contentDescription = "Skip"
                )
            }
    }
    }
}