package iss.nus.edu.sg.mygo.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Flight(
    val flightId: Int,
    val flightNumber: String,
    val airline: String,
    val departureCity: String,
    val arrivalCity: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: String,  // 这里改成 String
    val arrivalTime: String,    // 这里改成 String
    val duration: String,
    val seatAvailability: Map<String, Int>,
    val seatType: Map<String, String>,
    val flightStatus: String,
) {
    // 解析 LocalDateTime
    fun getFormattedDepartureTime(): LocalDateTime =
        LocalDateTime.parse(departureTime, DateTimeFormatter.ISO_DATE_TIME)

    fun getFormattedArrivalTime(): LocalDateTime =
        LocalDateTime.parse(arrivalTime, DateTimeFormatter.ISO_DATE_TIME)
}
