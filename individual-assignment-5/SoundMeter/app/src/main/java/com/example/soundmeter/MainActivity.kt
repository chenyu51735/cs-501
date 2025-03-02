package com.example.soundmeter

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
import com.example.soundmeter.ui.theme.SoundMeterTheme
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.log10
import kotlin.math.sqrt
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var hasAudioPermission by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestMicrophonePermission()
        setContent {
            SoundMeter(hasAudioPermission)
        }
    }

    private fun requestMicrophonePermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                hasAudioPermission = isGranted
            }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            hasAudioPermission = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}
@Composable
fun SoundMeter(hasPermission: Boolean) {
    val context = LocalContext.current
    var decibels by remember { mutableStateOf(0f) }
    val noiseThreshold = 80f

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            while (true) {
                decibels = startAudioRecording(context) ?: 0f
                delay(500)
            }
        }
    }

    val color = when {
        decibels > noiseThreshold -> Color.Red
        decibels > 50 -> Color.Yellow
        else -> Color.Green
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sound Level: ${decibels.toInt()} dB", fontSize = 24.sp, color = Color.White)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = decibels / 120f)
                    .height(30.dp)
                    .background(color)
            )
        }
        if (decibels > noiseThreshold) {
            Text("Warning: Noise Level Too High!", fontSize = 20.sp, color = Color.Red)
        }
    }
}



fun startAudioRecording(context: android.content.Context): Float? {
    val sampleRate = 44100
    val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        return null
    }

    val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
    )

    val buffer = ShortArray(bufferSize)
    audioRecord.startRecording()
    audioRecord.read(buffer, 0, bufferSize)
    audioRecord.stop()
    audioRecord.release()

    val rms = sqrt(buffer.map { it.toDouble() * it / buffer.size }.sum())
    return if (rms > 0) 20 * log10(rms).toFloat() else 0f
}
