package com.pesto.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.common.base.Verify.verify

import com.pesto.app.data.model.Task
import com.pesto.app.data.model.User
import com.pesto.app.data.repository.TaskRepository
import com.pesto.app.ui.home.TaskViewModel
import com.pesto.app.util.UiState
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class

TaskViewModelTest {
    @get:Rule


    private val repository: TaskRepository = mock()
    private val viewModel = TaskViewModel(repository)

    @Test
    fun addTask_shouldEmitLoadingAndSuccessStates() {
        val task = Task(id = "1", title = "Test Task", description = "This is a test task")

        whenever(repository.addTask(task)).thenReturn(MutableLiveData(UiState.Success(Pair(task, "Task added successfully"))))

        val observer = mock<Observer<UiState<Pair<Task,String>>>>()
        viewModel.addTask.observe(this, observer)

        viewModel.addTask(task)

        verify(repository).addTask(task)
        verify(observer).onChanged(UiState.Loading)
        verify(observer).onChanged(UiState.Success(Pair(task, "Task added successfully")))
    }

    @Test
    fun addTask_shouldEmitErrorStateIfRepositoryFails() {
        val task = Task(id = "1", title = "Test Task", description = "This is a test task")

        whenever(repository.addTask(task)).thenReturn(MutableLiveData(UiState.Error("An error occurred while adding task")))

        val observer = mock<Observer<UiState<Pair<Task,String>>>>()
        viewModel.addTask.observe(this, observer)

        viewModel.addTask(task)

        verify(repository).addTask(task)
        verify(observer).onChanged(UiState.Loading)
        verify(observer).onChanged(UiState.Error("An error occurred while adding task"))
    }

    @Test
    fun updateTask_shouldEmitLoadingAndSuccessStates() {
        val task = Task(id = "1", title = "Updated Task", description = "This is an updated task")

        whenever(repository.updateTask(task)).thenReturn(MutableLiveData(UiState.Success(Pair(task, "Task updated successfully"))))

        val observer = mock<Observer<UiState<Pair<Task,String>>>>()
        viewModel.updateTask.observe(this, observer)

        viewModel.updateTask(task)

        verify(repository).updateTask(task)
        verify(observer).onChanged(UiState.Loading)
        verify(observer).onChanged(UiState.Success(Pair(task, "Task updated successfully")))
    }

    @Test
    fun updateTask_shouldEmitErrorStateIfRepositoryFails() {
        val task = Task(id = "1", title = "Updated Task", description = "This is an updated task")

        whenever(repository.updateTask(task)).thenReturn(MutableLiveData(UiState.Error("An error occurred while updating task")))

        val observer = mock<Observer<UiState<Pair<Task,String>>>>()
        viewModel.updateTask.observe(this, observer)

        viewModel.updateTask(task)

        verify(repository).updateTask(task)
        verify(observer).onChanged(UiState.Loading)
        verify(observer).onChanged(UiState.Error("An error occurred while updating task"))
    }
}