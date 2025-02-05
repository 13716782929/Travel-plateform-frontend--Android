package iss.nus.edu.sg.mygo.models

import iss.nus.edu.sg.mygo.enum.RoomType

data class Hotel (
    val hotelId: Int,
    val hotelName: String,
    val location: String,
    val description: String,
    val amenities: String,
    val roomType: RoomType,
    val roomAvailability: Boolean
)
