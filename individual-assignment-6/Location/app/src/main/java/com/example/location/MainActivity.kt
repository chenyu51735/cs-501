package com.example.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        setContent {
            MaterialTheme {
                LocationMapScreen(fusedLocationClient, locationRequest) { callback ->
                    locationCallback = callback
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

@Composable
fun LocationMapScreen(
    fusedLocationClient: FusedLocationProviderClient,
    locationRequest: LocationRequest,
    onCallbackReady: (LocationCallback) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var address by remember { mutableStateOf("Address not found") }
    val markerList = remember { mutableStateListOf<LatLng>() }
    val cameraPositionState = rememberCameraPositionState()

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (granted) {
            val callback = startLocationUpdates(
                context, fusedLocationClient, locationRequest, cameraPositionState
            ) { latLng ->
                userLocation = latLng
                scope.launch {
                    address = reverseGeocode(context, latLng) ?: "Address not found"
                }
            }
            onCallbackReady(callback)
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            val callback = startLocationUpdates(
                context, fusedLocationClient, locationRequest, cameraPositionState
            ) { latLng ->
                userLocation = latLng
                scope.launch {
                    address = reverseGeocode(context, latLng) ?: "Address not found"
                }
            }
            onCallbackReady(callback)
        }
    }

    Column {
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    markerList.add(latLng)
                },
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                userLocation?.let {
                    Marker(state = MarkerState(it), title = "You are here")
                }

                markerList.forEach {
                    Marker(state = MarkerState(it), title = "Custom Marker")
                }
            }
        }

        Text(
            text = "Your Address: $address",
            modifier = Modifier.padding(8.dp)
        )
    }
}

fun startLocationUpdates(
    context: android.content.Context,
    client: FusedLocationProviderClient,
    request: LocationRequest,
    cameraState: CameraPositionState,
    onLocation: (LatLng) -> Unit
): LocationCallback {
    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                onLocation(latLng)
                cameraState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
            }
        }
    }

    try {
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
    } catch (e: SecurityException) {
        e.printStackTrace()
    }

    return callback
}

suspend fun reverseGeocode(context: android.content.Context, latLng: LatLng): String? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
