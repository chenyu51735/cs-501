package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _filter = MutableStateFlow("All")
    val filter = _filter.asStateFlow()

    val tasks = repository.tasks
        .combine(_filter) { tasks, filter ->
            when (filter) {
                "Completed" -> tasks.filter { it.isCompleted }
                "Pending" -> tasks.filter { !it.isCompleted }
                else -> tasks
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(value: String) {
        _filter.value = value
    }

    fun toggleComplete(task: Task) = viewModelScope.launch {
        repository.update(task.copy(isCompleted = !task.isCompleted))
    }

    fun addTask(title: String) = viewModelScope.launch {
        repository.insert(Task(title = title))
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
    fun updateTitle(task: Task, newTitle: String) = viewModelScope.launch {
        repository.update(task.copy(title = newTitle))
    }
}
@Suppress("UNCHECKED_CAST")
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
