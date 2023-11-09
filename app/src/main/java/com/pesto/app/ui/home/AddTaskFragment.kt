package com.pesto.app.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import com.pesto.app.R
import com.pesto.app.data.model.Task
import com.pesto.app.databinding.FragmentAddTaskBinding
import com.pesto.app.ui.auth.AuthViewModel
import com.pesto.app.util.UiState
import com.pesto.app.util.snackbar

@AndroidEntryPoint
class AddTaskFragment(private val task: Task? = null) : BottomSheetDialogFragment() {

    val TAG: String = "AddTaskFragment"
    lateinit var binding: FragmentAddTaskBinding
    private var closeFunction: ((Boolean) -> Unit)? = null
    private var saveTaskSuccess: Boolean = false
    private var isValid = true
    val viewModel: TaskViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_add_task, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            binding = FragmentAddTaskBinding.bind(view)

            task?.let {
                binding.taskName.setText(it.title)
                binding.taskDescription.setText(it.description)
                binding.dueDateDropdown.setText(it.dueDate)
                binding.priorityDropdown.setText(it.status)
                binding.locationTextField.setText(it.location)
            }

            val priorityArray = resources.getStringArray(R.array.status)
            val arrayAdapter = activity?.let { ArrayAdapter(it, R.layout.dropdown_item, priorityArray) }
            binding.priorityDropdown.setAdapter(arrayAdapter)

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .build()
            binding.dueDateDropdown.setOnClickListener {
                datePicker.show(childFragmentManager, "DATE_PICKER")
            }
            datePicker.addOnPositiveButtonClickListener {
                binding.dueDateDropdown.setText(datePicker.headerText)
            }

            binding.addTaskButton.setOnClickListener {

                if (!validateTask()) {
                    return@setOnClickListener
                }
                val title = binding.taskName.text.toString()
                val description = binding.taskDescription.text.toString()
                val status = binding.priorityDropdown.text.toString()
                val date = binding.dueDateDropdown.text.toString()
                val location = binding.locationTextField.text.toString()

                val task = Task(title = title, description = description, status = status, dueDate = date, location = location)
                if (task.id == "") {
                    viewModel.addTask(task)
                }else {
                    viewModel.updateTask(task)
                }
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, HomeFragment())
                    ?.commit();
            }
            observer()
        }
    private fun validateTask(): Boolean {
        val title = binding.taskName.text.toString()
        val description = binding.taskDescription.text.toString()
        val status = binding.priorityDropdown.text.toString()
        val date = binding.dueDateDropdown.text.toString()
        val location = binding.locationTextField.text.toString()

        // Validate task name
        if (title.isEmpty()) {
            binding.taskName.error = "Please enter a task name"
            return false
        }

        // Validate description
        if (description.isEmpty()) {
            binding.taskDescription.error = "Please enter a task description"
            return false
        }

        // Validate status
        if (status.isEmpty()) {
            binding.priorityDropdown.error = "Please select a priority"
            return false
        }

        // Validate due date
        if (date.isEmpty()) {
            binding.dueDateDropdown.error = "Please select a due date"
            return false
        }

        // Validate location
        if (location.isEmpty()) {
            binding.locationTextField.error = "Please enter a task location"
            return false
        }

        return true
    }
    fun observer() {
        viewModel.addTask.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.addTaskButton.isEnabled = false
                } is UiState.Success -> {
                    saveTaskSuccess = true
                    snackbar("Task added successfully")
                    closeFunction?.invoke(true)
                    this.dismiss()
                } is UiState.Failure -> {
                    snackbar("Save failed, please try again")
                }
            }
        }

        viewModel.updateTask.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.addTaskButton.isEnabled = false
                } is UiState.Success -> {
                    saveTaskSuccess = true
                    snackbar("Task updated successfully")
                    closeFunction?.invoke(true)
                    this.dismiss()
                } is UiState.Failure -> {
                    snackbar("Save failed, please try again")
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        closeFunction?.invoke(saveTaskSuccess)
    }

    fun setDismissListener(function: ((Boolean) -> Unit)?) {
        closeFunction = function
    }
}