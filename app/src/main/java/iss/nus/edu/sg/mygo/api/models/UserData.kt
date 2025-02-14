package iss.nus.edu.sg.mygo.api.models

import com.google.gson.annotations.SerializedName

// the model to request data from API
data class RegisterRequest(
    @SerializedName ("email") val email: String,
    @SerializedName ("password") val password: String,
    @SerializedName ("preference") val preference: String?
)
data class LoginRequest(
    @SerializedName ("email") val email: String,
    @SerializedName ("password") val password: String
)

data class LoginResponse(
    @SerializedName ("token") val token: String,
    @SerializedName ("userId") val userId: String,
    @SerializedName ("name") val name: String,
    @SerializedName ("email") val email: String,
    @SerializedName ("role") val role: String)
