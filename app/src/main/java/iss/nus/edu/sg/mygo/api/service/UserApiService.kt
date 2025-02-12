package iss.nus.edu.sg.mygo.api.service
/*
Class name: UserApiService
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date:
Version
*/

import iss.nus.edu.sg.mygo.api.models.AttractionBookingRequest
import iss.nus.edu.sg.mygo.api.models.LoginRequest
import iss.nus.edu.sg.mygo.api.models.LoginResponse
import iss.nus.edu.sg.mygo.api.models.RegisterRequest
import iss.nus.edu.sg.mygo.models.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET

interface UserApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/attractions/booking")
    suspend fun createAttractionBooking(@Body request: AttractionBookingRequest): Response<AttractionBookingRequest>

    @GET("/api/user/profile")
    suspend fun getUserProfile(@Header("Authorisation") token: String): User

    companion object {
        val private: Any = TODO()
        private const val BASE_URL = "http://10.0.2.2:8080/"

        fun create(): UserApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserApiService::class.java)
        }


    }
}