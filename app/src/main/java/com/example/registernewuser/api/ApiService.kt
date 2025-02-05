package com.example.registernewuser.api


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Data Classes for API Requests
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String?,
    val status: String?,
    val gender: String?,
    val countryOfOrigin: String?,
    val dateOfBirth: String?,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

// Data Class for API Responses
data class ApiResponse(
    val message: String,
    val token: String? // Can store a login token here
)

// API Interface
interface ApiService {
    @POST("/register")
    fun registerUser(@Body request: RegisterRequest): Call<ApiResponse>

    @POST("/login")
    fun loginUser(@Body request: LoginRequest): Call<ApiResponse>
}
