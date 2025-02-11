package iss.nus.edu.sg.mygo.models


data class HotelBooking(
    val hotelBookingId: Int,
    val bookingId: Int,
    val hotelId: Int,
    val hotelUuid: String?, // nullable，后端可能找不到 UUID
    // about to show
    val hotelName: String,
    val location: String,
    val checkInDate: String,
    val checkOutDate: String,
    val guests: Int,
    val roomType: String,
    val totalAmount: Double, // ✅ 添加 totalAmount
    val hotelImageUuid: String
)