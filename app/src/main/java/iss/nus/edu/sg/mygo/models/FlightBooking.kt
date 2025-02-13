package iss.nus.edu.sg.mygo.models

data class FlightBooking(
    val bookingId: Int,
    val flightId: Int,
    val seatClass: String,
    val passengerName: String,
    val passengerId: String,
    val status: String,
)