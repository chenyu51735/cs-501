package com.example.altitude

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
import com.example.altitude.ui.theme.AltitudeTheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null
    private var _pressure by mutableStateOf(1013.25f) // Default sea level pressure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        setContent {
            Altitude(pressure = _pressure)
        }
    }

    override fun onResume() {
        super.onResume()
        pressureSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_PRESSURE) {
                _pressure = it.values[0]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun Altitude(pressure: Float) {
    val altitude = calculateAltitude(pressure)
    val backgroundColor by animateColorAsState(targetValue = altitudeToColor(altitude))

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).background(backgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Altitude: ${altitude.toInt()} m", fontSize = 32.sp)
        Text("Pressure: ${pressure} hPa", fontSize = 24.sp)
    }
}

fun calculateAltitude(pressure: Float): Float {
    val P0 = 1013.25f
    return 44330 * (1 - (pressure / P0).pow(1 / 5.255f))
}

fun altitudeToColor(altitude: Float): Color {
    return when {
        altitude < 1 -> Color(0x00000000)
        altitude < 2000 -> Color(0xFF87CEEB)
        altitude < 4000 -> Color(0xFF4682B4)
        altitude < 8000 -> Color(0xFF2E3B55)
        else -> Color(0xFF1C1C1C)
    }
}
