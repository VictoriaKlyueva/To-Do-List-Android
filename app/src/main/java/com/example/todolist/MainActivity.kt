package com.example.todolist

import android.annotation.SuppressLint
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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var tasks = ArrayList<Task>()
    private lateinit var apiService: ApiService
    private val BASE_URL = "http://192.168.232.82:5259/api/"
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

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        val editTaskName: EditText = findViewById(R.id.editTaskName)

        // Add Button Click Listener
        val addButton: Button = findViewById(R.id.buttonAddTask)
        addButton.setOnClickListener {
            val taskName = editTaskName.text
            if (taskName.isBlank()) {
                Toast.makeText(this, "Описание не может быть пустым", Toast.LENGTH_SHORT).show()
            } else {
                editTaskName.setText("")
                createTask(taskName, adapter)
            }
        }

        // Get tasks list with API
        loadTasks()

        // Adapter for listView
        val listView = findViewById<ListView>(R.id.listView)
        adapter = TaskArrayAdapter(this, tasks, apiService)
        listView.adapter = adapter
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            try {
                val taskList = apiService.getTasks()
                tasks.clear()
                tasks.addAll(taskList)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createTask(taskName: Editable, adapter: TaskArrayAdapter) {
        val newTask = Task(description=taskName.toString())
        tasks.add(newTask)

        // Post new task API call
        apiService.createTask(newTask).enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {
                    println("Task created successfully: ${response.body()}")
                } else {
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