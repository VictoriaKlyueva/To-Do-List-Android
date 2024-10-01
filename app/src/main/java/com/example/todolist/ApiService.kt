package com.example.todolist

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("todo")
    fun createTask(@Body task: Task): Call<Task>

    @GET("todo")
    suspend fun getTasks(): List<Task>
}
