package com.example.githubviewer


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class GitHubRepo(
    val id: Long,
    val name: String,
    val description: String?
)

interface GitHubApi {
    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 20
    ): Response<List<GitHubRepo>>
}

object GitHubApiClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: GitHubApi = retrofit.create(GitHubApi::class.java)
}

class GitHubRepoViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val repos: List<GitHubRepo> = emptyList(),
        val error: String? = null,
        val hasNextPage: Boolean = true,
        val currentPage: Int = 1,
        val username: String = ""
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun search(username: String) {
        _uiState.value = UiState(isLoading = true, username = username)
        loadRepos(username, page = 1, append = false)
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (!state.isLoading && state.hasNextPage) {
            loadRepos(state.username, state.currentPage + 1, append = true)
        }
    }

    private fun loadRepos(username: String, page: Int, append: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = GitHubApiClient.api.getUserRepos(username, page)
                val repos = response.body() ?: emptyList()
                val hasNext = response.headers()["Link"]?.contains("rel=\"next\"") == true

                _uiState.update {
                    it.copy(
                        repos = if (append) it.repos + repos else repos,
                        currentPage = page,
                        isLoading = false,
                        hasNextPage = hasNext
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitHubRepoScreen(viewModel: GitHubRepoViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }

    Column(Modifier
        .padding(16.dp)
        .fillMaxSize()) {

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("GitHub Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = { viewModel.search(username) }) {
            Text("Search")
        }

        Spacer(Modifier.height(8.dp))

        when {
            state.isLoading && state.repos.isEmpty() -> {
                CircularProgressIndicator(Modifier.padding(16.dp))
            }
            state.error != null -> {
                Text("Error: ${state.error}", color = Color.Red)
            }
            else -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.repos) { repo ->
                        RepoItem(repo)
                    }
                    if (state.isLoading) {
                        item {
                            CircularProgressIndicator(Modifier.padding(16.dp))
                        }
                    } else if (state.hasNextPage) {
                        item {
                            Button(
                                onClick = { viewModel.loadNextPage() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text("Load More")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepoItem(repo: GitHubRepo) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(repo.name, style = MaterialTheme.typography.titleMedium)
        Text(repo.description ?: "", style = MaterialTheme.typography.bodyMedium)
        Divider()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GitHubRepoScreen()
            }
        }
    }
}