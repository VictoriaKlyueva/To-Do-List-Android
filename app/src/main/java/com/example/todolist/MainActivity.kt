package com.example.todolist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class MainActivity : AppCompatActivity() {
    private var tasks = ArrayList<Task>()
    private val PICK_FILE_REQUEST_CODE = 1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Адаптер данных для listView
        val listView = findViewById<ListView>(R.id.listView)

        val adapter = TaskArrayAdapter(this, tasks)
        listView.adapter = adapter

        val editTaskName: EditText = findViewById(R.id.editTaskName)

        // Обработчик кнопки добавления
        val addButton: Button = findViewById(R.id.buttonAddTask)
        addButton.setOnClickListener {
            val taskName = editTaskName.text
            editTaskName.setText("")
            createTask(taskName)
            adapter.notifyDataSetChanged();
        }
    }

    private fun createTask(taskName: Editable) {
        val newTask = Task(taskName.toString())
        tasks.add(newTask)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                uploadTasks(uri)
            }
        }
    }

    private fun uploadTasks(uri: Uri) {
        val json = contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }
        val gson = Gson()

        val taskListType = object : com.google.gson.reflect.TypeToken<List<Task>>() {}.type
        val loadedTasks: List<Task> = gson.fromJson(json, taskListType)

        tasks.clear()
        tasks.addAll(loadedTasks)
        (findViewById<ListView>(R.id.listView).adapter as TaskArrayAdapter).notifyDataSetChanged()

        Toast.makeText(
            this,
            "Tasks loaded from ${uri.lastPathSegment}",
            Toast.LENGTH_SHORT
        ).show()
    }
}