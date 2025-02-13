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
import iss.nus.edu.sg.mygo.models.HotelBooking
import iss.nus.edu.sg.mygo.models.Review
import iss.nus.edu.sg.mygo.models.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT

/**
 * @ClassName UserApiService
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/2/12
 * @Version 1.3
 */

interface UserApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // attraction booking
    @POST("/api/attractions/booking")
    suspend fun createAttractionBooking(@Body request: AttractionBookingRequest): Response<AttractionBookingRequest>

    @GET("/api/attractions/bookings/{userId}")
    suspend fun getUserAttractionBookings(@Path("userId") userId: Int): Response<List<AttractionBooking>>

    // 提交评论
    @POST("/api/reviews")
    suspend fun postReview(@Body request: ReviewRequest): Response<Review>

//    // 删除预约
//    @DELETE("/api/attractions/bookings/{bookingId}")
//    suspend fun deleteBooking(@Path("bookingId") bookingId: Int): Response<Unit>

    // hotel booking
    @POST("/api/hotels/booking")
    suspend fun createHotelBooking(@Body request: HotelBookingRequest): Response<HotelBookingRequest>

    @GET("/api/hotels/bookings/{userId}")
    suspend fun getUserHotelBookings(@Path("userId") userId: Int): Response<List<HotelBooking>>

    @GET("/api/attractions/{uuid}/reviews")
    suspend fun getAttractionReviews(@Path("uuid") uuid: String): Response<List<Review>>

    @GET("/api/hotels/{uuid}/reviews")
    suspend fun getHotelReviews(@Path("uuid") uuid: String): Response<List<Review>>

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