package com.example.staggeredphotogallery

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
import com.example.staggeredphotogallery.ui.theme.StaggeredPhotoGalleryTheme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.xmlpull.v1.XmlPullParser
import android.content.Context
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape

data class Photo(val imageName: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StaggeredPhotoGalleryTheme {
                StaggeredPhotoGallery()
                }
            }
        }
    }

@Composable
fun StaggeredPhotoGallery(){
    val context = LocalContext.current
    val photos = remember { loadPhotosFromXml(context) }
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }

    Box {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(200.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        )  {
            items(photos) { photo ->
                PhotoItem(photo = photo) {
                    selectedPhoto = photo
                }
            }
        }
        if (selectedPhoto != null) {
            EnlargedPhotoView(photo = selectedPhoto!!) {
                selectedPhoto = null
            }
        }
    }
}
fun loadPhotosFromXml(context: Context): List<Photo> {
    val photos = mutableListOf<Photo>()
    try {
        val parser: XmlPullParser = context.resources.getXml(R.xml.photos)
        var eventType = parser.eventType
        var currentTag: String? = null
        var imageName = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                    if (currentTag == "photo") {
                        imageName = ""
                    }
                }
                XmlPullParser.TEXT -> {
                    val text = parser.text.trim()
                    if (currentTag == "name") {
                        imageName = text
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "photo") {
                        photos.add(Photo(imageName))
                    }
                    currentTag = null
                }
            }
            eventType = parser.next()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return photos
}
@Composable
fun PhotoItem(photo: Photo, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        val context = LocalContext.current
        val imageId = remember(photo.imageName) {
            context.resources.getIdentifier(
                photo.imageName,
                "drawable",
                context.packageName
            )
        }
        Image(
            painter = painterResource(id = imageId),
            contentDescription = photo.imageName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EnlargedPhotoView(photo: Photo, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        val scale = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300)
            )
        }
        val context = LocalContext.current
        val imageId = remember(photo.imageName) {
            context.resources.getIdentifier(
                photo.imageName,
                "drawable",
                context.packageName
            )
        }
        Image(
            painter = painterResource(id = imageId),
            contentDescription = photo.imageName,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .scale(scale.value)
                .fillMaxWidth()
                .aspectRatio(1f)
        )
    }
}