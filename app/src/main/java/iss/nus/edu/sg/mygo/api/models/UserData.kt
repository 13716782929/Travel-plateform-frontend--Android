package iss.nus.edu.sg.mygo.api.models

// API 请求数据模型
data class RegisterRequest(val email: String, val password: String, val preference: String?)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val userId: String, val name: String, val email: String, val role: String)
