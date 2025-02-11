package iss.nus.edu.sg.mygo.models

data class FlightBooking(
    val flightBookingId: Int,
    val flightId: Int,
    val bookingId: Int,
    val departureTime: String,
    val arrivalTime: String
)