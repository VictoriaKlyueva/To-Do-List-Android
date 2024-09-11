package com.example.todolist

import android.widget.LinearLayout

class Task(
    val id: Int,
    val taskName: String,
    val layout: LinearLayout
) {
    var flag = false

    fun changeFlag() {
        flag = !flag
    }

    fun destroy() {
        layout.removeViews(1, layout.getChildCount() - 1)
    }
}