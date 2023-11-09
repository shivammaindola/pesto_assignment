package com.pesto.app.data.repository

import com.pesto.app.data.model.Task
import com.pesto.app.data.model.User
import com.pesto.app.util.UiState

interface TaskRepository {
    fun addTask(task: Task, result: (UiState<Pair<Task,String>>) -> Unit)
    fun updateTask(task: Task, result: (UiState<Pair<Task,String>>) -> Unit)
    fun deleteTask(task: Task, result: (UiState<Pair<Task,String>>) -> Unit)
    fun getTask(id: String, result: (UiState<Pair<Task,String>>) -> Unit)
    fun getTasks(user: User?, result: (UiState<List<Task>>) -> Unit)
    fun storeTasks(tasks: List<Task>, result: (UiState<String>) -> Unit)
}