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
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column

data class Product(val name: String, val price: String, val description: String)

val products = listOf(
    Product(name = "Apple", price = "$1.00", description = "A crisp and juicy apple, perfect for a healthy snack."),
    Product(name = "Banana", price = "$0.50", description = "A ripe and sweet banana, great for smoothies or on-the-go energy."),
    Product(name = "Orange", price = "$0.75", description = "A fresh and tangy orange, packed with vitamin C.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Multipane_shopping_appTheme {
                ShoppingApp(products)
            }

        }
    }
}

@Composable
fun ShoppingApp(products: List<Product>) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(1f)) {
                ProductList(
                    products = products,
                    onProductSelected = { selectedProduct = it }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                ProductDetails(
                    product = selectedProduct,
                    onBackClicked = { selectedProduct = null },
                    showBackButton = false
                )
            }
        }
    } else {
        if (selectedProduct == null) {
            ProductList(
                products = products,
                onProductSelected = { selectedProduct = it }
            )
        } else {
            ProductDetails(
                product = selectedProduct,
                onBackClicked = { selectedProduct = null },
                showBackButton = true
            )
        }
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    onProductSelected: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(products) { product ->
            Text(
                text = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProductSelected(product) }
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun ProductDetails(
    product: Product?,
    modifier: Modifier = Modifier,
    showBackButton: Boolean,
    onBackClicked: () -> Unit,
) {
    Column(modifier = modifier.padding(16.dp)) {
        if (showBackButton) {
            Text(
                text = "Back",
                modifier = Modifier
                    .clickable { onBackClicked() }
                    .padding(bottom = 16.dp)
            )
        }
        if (product != null) {
            Text(text = product.name)
            Text(text = product.price)
            Text(text = product.description)
        } else {
            Text(text = "Select a product to view details.")
        }
    }
}