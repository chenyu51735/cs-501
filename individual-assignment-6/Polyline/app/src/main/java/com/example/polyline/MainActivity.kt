package com.example.polyline
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TrailMapScreen()
            }
        }
    }
}

@Composable
fun TrailMapScreen() {
    val cameraPositionState = rememberCameraPositionState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            LatLng(37.7749, -122.4194), 14f
        )
    }

    val trailPath = listOf(
        LatLng(37.761332, -122.426272),
        LatLng(37.762239, -122.412830),
        LatLng(37.769629, -122.410708),
        LatLng(37.775925, -122.402626)
    )

    val parkArea = listOf(
        LatLng(37.761219, -122.428288),
        LatLng(37.758111, -122.428083),
        LatLng(37.758311, -122.425965),
        LatLng(37.761332, -122.426272),
    )

    val colorOptions = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Cyan, Color(0xFFFF9800) // orange
    )

    var polylineColorIndex by remember { mutableStateOf(0) }
    var polygonColorIndex by remember { mutableStateOf(1) }

    var polylineColor by remember { mutableStateOf(colorOptions[polylineColorIndex]) }
    var polylineWidth by remember { mutableStateOf(8f) }

    var polygonColor by remember { mutableStateOf(colorOptions[polygonColorIndex]) }
    var polygonStrokeWidth by remember { mutableStateOf(4f) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                Polyline(
                    points = trailPath,
                    color = polylineColor,
                    width = polylineWidth,
                    clickable = true,
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Trail: 1.5 miles, easy hike")
                        }
                    }
                )

                Polygon(
                    points = parkArea,
                    fillColor = polygonColor.copy(alpha = 0.3f),
                    strokeColor = polygonColor,
                    strokeWidth = polygonStrokeWidth,
                    clickable = true,
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Mission Dolores Park: Dolores St &, 19th St, San Francisco, CA 94114")
                        }
                    }
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Polyline Width: ${polylineWidth.toInt()}")
                Slider(
                    value = polylineWidth,
                    onValueChange = { polylineWidth = it },
                    valueRange = 1f..20f
                )

                Button(onClick = {
                    polylineColorIndex = (polylineColorIndex + 1) % colorOptions.size
                    polylineColor = colorOptions[polylineColorIndex]
                }, modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("Change Polyline Color")
                }

                Text("Polygon Stroke Width: ${polygonStrokeWidth.toInt()}")
                Slider(
                    value = polygonStrokeWidth,
                    onValueChange = { polygonStrokeWidth = it },
                    valueRange = 1f..20f
                )

                Button(onClick = {
                    polygonColorIndex = (polygonColorIndex + 1) % colorOptions.size
                    polygonColor = colorOptions[polygonColorIndex]
                }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Change Polygon Color")
                }
            }
        }
    }
}
