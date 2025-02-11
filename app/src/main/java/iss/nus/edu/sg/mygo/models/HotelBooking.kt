package iss.nus.edu.sg.mygo.models

import iss.nus.edu.sg.mygo.enum.RoomType

data class HotelBooking(
    val hotelBookingId: Int,
    val bookingId: Int,
    val hotelUuid: String?, // nullable，后端可能找不到 UUID
    // about
    val hotelName: String,
    val location: String,
    val checkInDate: String,
    val checkOutDate: String,
    val guestNumber: Int,
    val roomType: RoomType,
    val totalAmount: Double, // ✅ 添加 totalAmount
    val hotelImageUuid: String
)