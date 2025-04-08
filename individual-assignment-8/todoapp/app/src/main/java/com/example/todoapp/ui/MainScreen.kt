package com.example.todoapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.components.FilterBar
import com.example.todoapp.ui.components.TaskItem
import com.example.todoapp.viewmodel.TaskViewModel

@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val filter by viewModel.filter.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        FilterBar(selected = filter, onFilterChange = { viewModel.setFilter(it) })

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onCheckedChange = { viewModel.toggleComplete(task) },
                    onDelete = { viewModel.deleteTask(task) },
                    onTitleChange = { newTitle -> viewModel.updateTitle(task, newTitle) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FloatingActionButton(onClick = {
            viewModel.addTask("New Task") // Replace with dialog later
        }) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }
}

