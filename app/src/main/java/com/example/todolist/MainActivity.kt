package com.example.todolist

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var tasks = ArrayList<Task>()
    private lateinit var apiService: ApiService
    private val BASE_URL = "http://192.168.232.82:7067/api/"
    lateinit var adapter: TaskArrayAdapter

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

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Adapter for listView
        val listView = findViewById<ListView>(R.id.listView)
        adapter = TaskArrayAdapter(this, tasks)
        listView.adapter = adapter

        val editTaskName: EditText = findViewById(R.id.editTaskName)

        // Add Button Click Listener
        val addButton: Button = findViewById(R.id.buttonAddTask)
        addButton.setOnClickListener {
            val taskName = editTaskName.text
            editTaskName.setText("")
            createTask(taskName, adapter)
        }

        // Get tasks list with API
        loadTasks()
    }

    private fun loadTasks() {
        // Запуск корутины для получения задач
        lifecycleScope.launch {
            try {
                val taskList = apiService.getTasks()
                tasks.clear()
                tasks.addAll(taskList) // Добавляем полученные задачи в список
                adapter.notifyDataSetChanged() // Уведомляем адаптер об изменениях
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    private fun createTask(taskName: Editable, adapter: TaskArrayAdapter) {
        val newTask = Task(Description=taskName.toString())
        tasks.add(newTask)

        println("Я ебала это все")
        println(newTask)

        // Post new task API call
        apiService.createTask(newTask).enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    println("Task created successfully: ${response.body()}")
                } else {
                    // Получение исходного тела с ошибкой
                    val errorBody = response.errorBody()?.string()
                    println("Failed to create task: $errorBody")
                }
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}