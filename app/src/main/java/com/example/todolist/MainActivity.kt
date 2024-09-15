package com.example.todolist

import android.os.Bundle
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
            createTask(editTaskName.text)
            adapter.notifyDataSetChanged();
        }

        // Обработчик кнопки скачивания
        val saveButton: Button = findViewById(R.id.saveTasks)
        saveButton.setOnClickListener {
            saveTasks()
        }

        // Обработчик кнопки загрузки
        val uploadButton: Button = findViewById(R.id.uploadTasks)
        uploadButton.setOnClickListener {
            uploadTasks()
        }
    }

    private fun createTask(taskName: Editable) {
        val newTask = Task(taskName.toString())
        tasks.add(newTask)
    }

    private fun saveTasks() {
        val gson = Gson()
        val json = gson.toJson(tasks)
        val fileName = "tasks.json"
        val externalStorage = getExternalFilesDir(null)

        val file = File(externalStorage, fileName)
        // Записываем JSON в файл
        FileOutputStream(file).use { fos ->
            OutputStreamWriter(fos).use { writer ->
                writer.write(json)
            }
        }
        // Уведомление о успешном сохранении
        Toast.makeText(
            this,
            "Tasks saved as $fileName",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun uploadTasks() {
        val fileName = "tasks.json"
        val externalStorage = getExternalFilesDir(null)

        val file = File(externalStorage, fileName)
        val json = file.readText()
        val gson = Gson()

        val taskListType = object : com.google.gson.reflect.TypeToken<List<Task>>() {}.type
        val loadedTasks: List<Task> = gson.fromJson(json, taskListType)

        tasks.clear()
        tasks.addAll(loadedTasks)
        (findViewById<ListView>(R.id.listView).adapter as TaskArrayAdapter).notifyDataSetChanged()

        Toast.makeText(
            this,
            "Tasks loaded from $fileName",
            Toast.LENGTH_SHORT
        ).show()
    }
}