package com.example.todolist

data class Task(
    val id: Int = 0,
    val description: String,
    var isCompleted: Boolean = false
)