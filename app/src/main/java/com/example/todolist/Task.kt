package com.example.todolist

data class Task(
    val Id: Int = 0,
    val Description: String,
    var IsCompleted: Boolean = false
)