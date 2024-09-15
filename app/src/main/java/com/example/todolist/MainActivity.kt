package com.example.todolist

import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


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
        val button: Button = findViewById(R.id.buttonAddTask)
        button.setOnClickListener {
            createTask(editTaskName.text)
            adapter.notifyDataSetChanged();
        }
    }

    private fun createTask(taskName: Editable) {
        val newTask = Task(taskName.toString())
        tasks.add(newTask)
    }
}