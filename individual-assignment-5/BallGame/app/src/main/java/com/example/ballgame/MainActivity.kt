package com.example.ballgame

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
import com.example.ballgame.ui.theme.BallGameTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GyroscopeGame()
        }
    }
}

@Composable
fun GyroscopeGame() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(SensorManager::class.java) }
    var ballPosition by remember { mutableStateOf(Offset(300f, 600f)) }
    val ballRadius = 40f
    val screenWidth = 800f
    val screenHeight = 1400f
    val walls = listOf(
        Offset(200f, 400f) to Offset(600f, 450f),
        Offset(400f, 700f) to Offset(450f, 1100f)
    )

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.let { values ->
                    val dx = values[0] * -15
                    val dy = values[1] * 15

                    val newX = min(max(ballPosition.x + dx, ballRadius), screenWidth - ballRadius)
                    val newY = min(max(ballPosition.y + dy, ballRadius), screenHeight - ballRadius)

                    val newPosition = Offset(newX, newY)
                    if (!collidesWithWalls(newPosition, ballRadius, walls)) {
                        ballPosition = newPosition
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(sensorManager) {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color.Red, radius = ballRadius, center = ballPosition)

            for (wall in walls) {
                drawRoundRect(
                    color = Color.Gray,
                    topLeft = wall.first,
                    size = androidx.compose.ui.geometry.Size(
                        wall.second.x - wall.first.x,
                        wall.second.y - wall.first.y
                    ),
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }
        }
    }
}

fun collidesWithWalls(ballPos: Offset, ballRadius: Float, walls: List<Pair<Offset, Offset>>): Boolean {
    for ((topLeft, bottomRight) in walls) {
        if (ballPos.x + ballRadius > topLeft.x && ballPos.x - ballRadius < bottomRight.x &&
            ballPos.y + ballRadius > topLeft.y && ballPos.y - ballRadius < bottomRight.y) {
            return true
        }
    }
    return false
}