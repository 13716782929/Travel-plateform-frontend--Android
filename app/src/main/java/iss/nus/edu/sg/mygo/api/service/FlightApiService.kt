package iss.nus.edu.sg.mygo.api.service

/**
 * @ClassName FlightApiService
 * @Description
 * @Author Siti Alifah Binte Yahya
 * @StudentID A0295324B
 * @Date 13 Feb 2025
 * @Version 1.0
 */
import iss.nus.edu.sg.mygo.api.models.FlightSearchRequest
import iss.nus.edu.sg.mygo.models.FlightInfo
import iss.nus.edu.sg.mygo.models.FlightBookingRequest
import iss.nus.edu.sg.mygo.models.Flight
import iss.nus.edu.sg.mygo.models.FlightBooking
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
* Purpose: handles communication with the backend via retrofit to fetch flight result*/

//API (Model): FlightApiService.kt to communicate with backend
interface FlightApiService {

    @GET("api/flights/{id}")
    fun getFlightById(@Path("id") flightId: Int): Call<Flight>

//    @POST("api/flights/booking")
//    fun bookFlight(@Body request: FlightBookingRequest): Call<FlightBooking>
    @POST("api/flights/booking")
    suspend fun bookFlight(@Body request: FlightBookingRequest): Response<FlightBooking>


    @POST("api/flights")
    suspend fun getFlights(@Body request: FlightSearchRequest): List<FlightInfo>

    //create a retrofit instance
    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/"

        fun create(): FlightApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FlightApiService::class.java)
        }
    }
}