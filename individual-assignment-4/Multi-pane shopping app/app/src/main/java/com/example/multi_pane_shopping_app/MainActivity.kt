package com.example.multi_pane_shopping_app

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
import com.example.multi_pane_shopping_app.ui.theme.Multipane_shopping_appTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import android.content.res.Configuration


data class Product(val name: String, val price: String, val description: String)

val products = listOf(
    Product("Product A", "$100", "This is a great product A."),
    Product("Product B", "$150", "This is product B with more features."),
    Product("Product C", "$200", "Premium product C."),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Multipane_shopping_appTheme {
                ShoppingApp()
            }

        }
    }
}

@Composable
fun ShoppingApp() {
    val configuration = LocalConfiguration.current
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row(modifier = Modifier.fillMaxSize()) {
            ProductList(products) { selectedProduct = it }
            Spacer(modifier = Modifier.width(8.dp))
            ProductDetails(selectedProduct)
        }
    } else {
        var showDetails by remember { mutableStateOf(false) }

        if (!showDetails) {
            ProductList(products) {
                selectedProduct = it
                showDetails = true
            }
        } else {
            ProductDetails(selectedProduct, onBack = { showDetails = false })
        }
    }
}

@Composable
fun ProductList(products: List<Product>, onProductSelected: (Product) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(products) { product ->
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onProductSelected(product) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = product.name,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun ProductDetails(product: Product?, onBack: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (onBack != null) {
            Button(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("Back")
            }
        }
        if (product != null) {
            Text(text = product.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.price, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
        } else {
            Text(
                text = "Select a product to view details.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}