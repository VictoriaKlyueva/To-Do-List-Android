package com.example.todolist

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskArrayAdapter(
    context: Context,
    tasks: List<Task>,
    private val receivedApiService: ApiService
) : ArrayAdapter<Task>(context, 0, tasks) {

    private class ViewHolder(view: View) {
        val checkbox: CheckBox = view.findViewById(R.id.checkBox)
        val taskText: TextView = view.findViewById(R.id.textView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewHolder: ViewHolder
        val rootView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false).also {
            viewHolder = ViewHolder(it)
            it.tag = viewHolder
        } ?: return super.getView(position, convertView, parent)

        // Если convertView существует, извлекаем viewHolder из тега
        viewHolder = rootView.tag as ViewHolder

        val currentTask = getItem(position)

        viewHolder.checkbox.isChecked = currentTask?.isCompleted ?: false
        viewHolder.taskText.text = currentTask?.description

        // Обработчик клика по тексту задачи
        viewHolder.taskText.setOnClickListener {
            showEditTaskDialog(currentTask)
        }

        viewHolder.deleteButton.setOnClickListener {
            deleteTask(currentTask)
        }

        // Обработчик клика по чекбоксу
        viewHolder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            currentTask?.let { task ->
                updateTaskCompletionStatus(task, isChecked)
                task.isCompleted = isChecked
            }
        }

        return rootView
    }

    private fun deleteTask(currentTask: Task?) {
        currentTask?.let { task ->
            receivedApiService.deleteTask(task.id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        println("Задача удалена")
                        remove(task)
                        notifyDataSetChanged()
                    } else {
                        println("Ошибка при удалении задачи: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    println("Неудача: ${t.message}")
                }
            })
        }
    }

    private fun updateTaskCompletionStatus(task: Task, isChecked: Boolean) {
        val apiCall = if (isChecked) {
            receivedApiService.makeTaskCompleted(task.id)
        } else {
            receivedApiService.makeTaskIncompleted(task.id)
        }

        apiCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("Статус задачи обновлен: ${if (isChecked) "выполнена" else "невыполнена"}")
                } else {
                    println("Ошибка при обновлении статуса задачи: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Неудача: ${t.message}")
            }
        })
    }

    private fun showEditTaskDialog(currentTask: Task?) {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)
        input.setText(currentTask?.description)
        builder.setTitle("Редактировать задачу")
        builder.setView(input)

        builder.setPositiveButton("Сохранить") { dialog, _ ->
            val newDescription = input.text.toString()
            currentTask?.description = newDescription
            notifyDataSetChanged()
            updateTaskDescription(currentTask, newDescription)
            dialog.dismiss()
        }

        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun updateTaskDescription(currentTask: Task?, newDescription: String) {
        currentTask?.let { task ->
            receivedApiService.changeTaskDescription(task.id, Description(newDescription))
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            println("Описание задачи обновлено")
                        } else {
                            println("Ошибка при обновлении задачи: ${response.code()}, ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        println("Ошибка при отправке запроса: ${t.message}")
                    }
                })
        }
    }
}
