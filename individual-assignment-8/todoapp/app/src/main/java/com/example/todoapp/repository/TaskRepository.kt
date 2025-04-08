package com.example.todoapp.repository

import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {
    val tasks: Flow<List<Task>> = dao.getAllTasks()

    suspend fun insert(task: Task) = dao.insertTask(task)
    suspend fun update(task: Task) = dao.updateTask(task)
    suspend fun delete(task: Task) = dao.deleteTask(task)
}
