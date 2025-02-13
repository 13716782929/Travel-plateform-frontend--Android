package iss.nus.edu.sg.mygo.models
/**
 * @ClassName FlightBooking
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/1/25
 * @Version 1.3
 */

data class FlightBooking(
    val flightBookingId: Int,
    val flightId: Int,
    val bookingId: Int,
    val departureTime: String,
    val arrivalTime: String
)