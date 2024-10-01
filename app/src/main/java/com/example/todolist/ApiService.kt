package com.example.todolist

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("todo")
    fun createTask(@Body task: Task): Call<Task>

    @GET("todo")
    suspend fun getTasks(): List<Task>

    @DELETE("todo/{id}")
    fun deleteTask(@Path("id") taskId: Int): Call<Void>
}
