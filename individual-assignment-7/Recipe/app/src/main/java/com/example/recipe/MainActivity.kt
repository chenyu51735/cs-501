package com.example.recipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class MealResponse(
    @Json(name = "meals") val meals: List<Meal>?
)

data class Meal(
    @Json(name = "strMeal") val name: String,
    @Json(name = "strMealThumb") val thumbnail: String,
    @Json(name = "strInstructions") val instructions: String
)

interface MealDbApi {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse
}

object ApiClient {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: MealDbApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(MealDbApi::class.java)
}

class RecipeViewModel : ViewModel() {

    sealed class RecipeState {
        object Initial : RecipeState()
        object Loading : RecipeState()
        data class Success(val meals: List<Meal>) : RecipeState()
        data class Error(val errorMessage: String) : RecipeState()
    }

    private val _recipeState = MutableStateFlow<RecipeState>(RecipeState.Initial)
    val recipeState: StateFlow<RecipeState> = _recipeState

    fun searchMeals(query: String) {
        viewModelScope.launch {
            _recipeState.value = RecipeState.Loading
            try {
                val response = ApiClient.api.searchMeals(query)
                val meals = response.meals ?: emptyList()
                _recipeState.value = RecipeState.Success(meals)
            } catch (e: Exception) {
                _recipeState.value = RecipeState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RecipeScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(viewModel: RecipeViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    val state by viewModel.recipeState.collectAsState()

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Recipes", fontSize = 20.sp) },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            if (query.isNotBlank()) {
                viewModel.searchMeals(query)
            }
        }) {
            Text("Search", fontSize = 20.sp)
        }

        when (state) {
            RecipeViewModel.RecipeState.Initial -> {}
            RecipeViewModel.RecipeState.Loading -> CircularProgressIndicator()
            is RecipeViewModel.RecipeState.Success -> {
                val meals = (state as RecipeViewModel.RecipeState.Success).meals
                LazyColumn {
                    items(meals) { meal ->
                        RecipeItem(meal)
                    }
                }
            }
            is RecipeViewModel.RecipeState.Error -> {
                val error = (state as RecipeViewModel.RecipeState.Error).errorMessage
                Text("Error: $error", color = Color.Red, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun RecipeItem(meal: Meal) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(meal.name, style = MaterialTheme.typography.titleMedium)
            Image(
                painter = rememberAsyncImagePainter(meal.thumbnail),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Text(meal.instructions.take(100) + "...", fontSize = 14.sp)
        }
    }
}
