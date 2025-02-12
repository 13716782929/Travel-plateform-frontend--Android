package iss.nus.edu.sg.mygo.api

import iss.nus.edu.sg.mygo.api.service.FlightApiService
import iss.nus.edu.sg.mygo.models.Flight
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FlightApiServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var flightApiService: FlightApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        flightApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // 确保这里是 "/"
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()
            .create(FlightApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getFlightById API`() = runBlocking {
        val mockResponse = MockResponse()
            .setBody(
                """{
                    "flightId": 1,
                    "flightNumber": "SQ001",
                    "airline": "Singapore Airlines",
                    "departureCity": "Singapore",
                    "arrivalCity": "Tokyo",
                    "departureAirport": "Changi Airport",
                    "arrivalAirport": "Narita Airport",
                    "departureTime": "2025-01-01T10:00:00",
                    "arrivalTime": "2025-01-01T18:00:00",
                    "duration": "8h",
                    "flightStatus": "Scheduled"
                }"""
            )
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val flight = flightApiService.getFlightById(1).execute().body()
        assertEquals(1, flight?.flightId)
        assertEquals("SQ001", flight?.flightNumber)
        assertEquals("Singapore Airlines", flight?.airline)
    }
}
