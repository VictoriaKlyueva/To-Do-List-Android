package com.example.todolist

data class Task(
    val id: Int = 0,
    var description: String,
    var isCompleted: Boolean = false
)