package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.TaskDatabase
import com.example.todoapp.repository.TaskRepository
import com.example.todoapp.ui.TaskListScreen
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = TaskDatabase.getDatabase(applicationContext)
        val dao = database.taskDao()
        val repository = TaskRepository(dao)
        val viewModelFactory = TaskViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        setContent {
            MaterialTheme {
                TaskListScreen(viewModel)
            }
        }
    }
}

