package iss.nus.edu.sg.mygo.api.service

import iss.nus.edu.sg.mygo.models.FlightBookingRequest
import iss.nus.edu.sg.mygo.models.Flight
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface FlightApiService {

    @GET("api/flights/{id}")
    fun getFlightById(@Path("id") flightId: Int): Call<Flight>

    @POST("api/flights/booking")
    fun bookFlight(@Body request: FlightBookingRequest): Call<Void>

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