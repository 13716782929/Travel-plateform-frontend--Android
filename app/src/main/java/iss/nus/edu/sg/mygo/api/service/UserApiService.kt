package iss.nus.edu.sg.mygo.api.service

import iss.nus.edu.sg.mygo.api.models.AttractionBookingRequest
import iss.nus.edu.sg.mygo.api.models.HotelBookingRequest
import iss.nus.edu.sg.mygo.api.models.LoginRequest
import iss.nus.edu.sg.mygo.api.models.LoginResponse
import iss.nus.edu.sg.mygo.api.models.RegisterRequest
import iss.nus.edu.sg.mygo.api.models.ReviewRequest
import iss.nus.edu.sg.mygo.models.Attraction
import iss.nus.edu.sg.mygo.models.AttractionBooking
import iss.nus.edu.sg.mygo.models.Booking
import iss.nus.edu.sg.mygo.models.Review
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/attractions/booking")
    suspend fun createAttractionBooking(@Body request: AttractionBookingRequest): Response<AttractionBookingRequest>

    @GET("/api/attractions/bookings/{userId}")
    suspend fun getUserBookings(@Path("userId") userId: Int): Response<List<AttractionBooking>>

    // 提交评论
    @POST("/api/reviews")
    suspend fun postReview(@Body request: ReviewRequest): Response<Review>

//    // 删除预约
//    @DELETE("/api/attractions/bookings/{bookingId}")
//    suspend fun deleteBooking(@Path("bookingId") bookingId: Int): Response<Unit>

    @POST("/api/hotels/booking")
    suspend fun createHotelBooking(@Body request: HotelBookingRequest): Response<HotelBookingRequest>

    companion object {
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