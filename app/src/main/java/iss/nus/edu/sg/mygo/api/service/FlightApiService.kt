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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

//API (Model): FlightApiService.kt to communicate with backend
interface FlightApiService {

    @POST("api/flights")
    suspend fun getFlights(@Body request: FlightSearchRequest): List<FlightInfo>

    companion object {
        private const val BASE_URL = "http://localhost:5174/"

        fun create(): FlightApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FlightApiService::class.java)
        }
    }
}
