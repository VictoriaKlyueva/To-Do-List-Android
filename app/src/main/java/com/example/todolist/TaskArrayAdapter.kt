package com.example.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskArrayAdapter(context: Context, tasks: List<Task>, receivedApiService: ApiService)
    : ArrayAdapter<Task>(context, 0, tasks) {
    private var apiService: ApiService = receivedApiService

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val rootView = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_item,
            parent,
            false
        )

        val currentTask = getItem(position)

        // Находим элементы представления
        val checkbox: CheckBox = rootView.findViewById(R.id.checkBox)
        val taskText: TextView = rootView.findViewById(R.id.textView)
        val deleteButton: Button = rootView.findViewById(R.id.deleteButton)

        // Устанавливаем данные
        taskText.text = currentTask?.description

        // Устанавливаем состояние чекбокса
        checkbox.isChecked = currentTask?.isCompleted ?: false

        // Обработчик для кнопки удаления
        deleteButton.setOnClickListener {
            currentTask?.let { task ->
                apiService.deleteTask(currentTask.id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            println("Задача успешно удалена.")
                        } else {
                            println("Ошибка при удалении задачи: ${response.code()} - ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        println("Неудача: ${t.message}")
                    }
                })
                remove(task)
                notifyDataSetChanged()
            }
        }

        // Обработчик для чекбокса
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            currentTask?.isCompleted = isChecked
        }

        return rootView
    }
}