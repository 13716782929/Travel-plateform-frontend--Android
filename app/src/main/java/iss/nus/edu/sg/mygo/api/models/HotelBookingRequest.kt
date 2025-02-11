package iss.nus.edu.sg.mygo.api.models

data class HotelBookingRequest(
    val hotelUuid: String,
    val userId:String,
    val checkInDate: String,
    val checkOutDate: String,
    val roomType: String,
    val numberOfGuests: Int
)