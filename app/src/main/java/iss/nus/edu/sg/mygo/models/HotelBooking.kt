package iss.nus.edu.sg.mygo.models

data class HotelBooking(
    val hotelBookingId: Int,
    val hotelId: Int,
    val bookingId: Int,
    val checkInDate: String,
    val checkOutDate: String
)