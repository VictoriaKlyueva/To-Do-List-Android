package com.example.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

class TaskArrayAdapter(context: Context, tasks: List<Task>)
    : ArrayAdapter<Task>(context, 0, tasks) {

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
        taskText.text = currentTask?.name

        // Устанавливаем состояние чекбокса
        checkbox.isChecked = currentTask?.flag ?: false

        // Обработчик для кнопки удаления
        deleteButton.setOnClickListener {
            currentTask?.let { task ->
                remove(task)
                notifyDataSetChanged()
            }
        }

        // Обработчик для чекбокса
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            currentTask?.flag = isChecked
        }

        return rootView
    }
}