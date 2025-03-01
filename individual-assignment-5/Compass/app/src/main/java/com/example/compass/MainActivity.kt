package com.example.compass

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
import com.example.compass.ui.theme.CompassTheme
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private var _azimuth by mutableStateOf(0f)
    private var lastAzimuth by mutableStateOf(0f)
    private var _pitch by mutableStateOf(0f)
    private var _roll by mutableStateOf(0f)

    private var gravityValues: FloatArray? = null
    private var geomagneticValues: FloatArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            Compass(azimuth = _azimuth, pitch = _pitch, roll = _roll)
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        magnetometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        gyroscope?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> gravityValues = it.values.clone()
                Sensor.TYPE_MAGNETIC_FIELD -> geomagneticValues = it.values.clone()
                Sensor.TYPE_GYROSCOPE -> {
                    _pitch = it.values[0]
                    _roll = it.values[1]
                }
            }
        }
        updateCompass()
    }

    private fun updateCompass() {
        if (gravityValues != null && geomagneticValues != null) {
            val rotationMatrix = FloatArray(9)
            val orientation = FloatArray(3)
            if (SensorManager.getRotationMatrix(rotationMatrix, null, gravityValues, geomagneticValues)) {
                SensorManager.getOrientation(rotationMatrix, orientation)
                val newAzimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                _azimuth = (newAzimuth + 360) % 360
                lastAzimuth = _azimuth
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun Compass(azimuth: Float, pitch: Float, roll: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Compass Heading: ${azimuth.toInt()}°", fontSize = 24.sp, color = Color.White)
        Text("Pitch: ${pitch.toInt()}°", fontSize = 24.sp, color = Color.White)
        Text("Roll: ${roll.toInt()}°", fontSize = 24.sp, color = Color.White)

        Canvas(modifier = Modifier.size(200.dp)) {
            rotate(azimuth) {
                drawLine(Color.Red, start = Offset(size.width / 2, size.height / 2), end = Offset(size.width / 2, 0f), strokeWidth = 8f)
            }
        }
    }
}

