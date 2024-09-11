package com.example.todolist

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.Task


class MainActivity : AppCompatActivity() {
    private var id: Int = 0
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

        val editTaskName: EditText = findViewById(R.id.editTaskName)

        val button: Button = findViewById(R.id.buttonAddTask)
        button.setOnClickListener {
            val taskName = editTaskName.text
            Log.d("DEBUGER", taskName.toString())

            // Add task view
            createTask(taskName)
        }
    }

    private fun findTaskById(id: Int) : Task? {
        for (task in tasks) {
            if (task.id == id)
                return task
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    private fun createTask(taskName: Editable) {
        // Create view for task
        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.id = id

        linearLayout.orientation = LinearLayout.HORIZONTAL

        // CheckBox
        val checkbox = CheckBox(this)
        checkbox.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        checkbox.id = id
        checkbox.setOnClickListener {
            findTaskById(checkbox.id)?.changeFlag()
        }
        linearLayout.addView(checkbox)

        // Task name
        val editText = EditText(this)
        editText.layoutParams = LinearLayout.LayoutParams(
            700,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        editText.text = taskName
        editText.id = id
        linearLayout.addView(editText)

        // Delete button
        val deleteButton = Button(this)
        deleteButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        deleteButton.text = "Удалить"
        deleteButton.id = id
        deleteButton.setOnClickListener {
            // Вывод для проверочки
            for(task in tasks) {
                Log.d("Debug", task.taskName)
                Log.d("Debug", task.flag.toString())
            }

            findTaskById(deleteButton.id)?.destroy()
        }
        linearLayout.addView(deleteButton)

        // Добавление в существующий layout
//        val tasksLayout: LinearLayout = findViewById(R.id.tasksLayout)
//        if (linearLayout.getParent() != null) {
//            (linearLayout.getParent() as ViewGroup).removeView(linearLayout)
//        }
//        tasksLayout.addView(linearLayout)

        setContentView(linearLayout)

        val newTask = Task(id, taskName.toString(), linearLayout)
        tasks.add(newTask)
        id += 1
    }
}