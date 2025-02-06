package iss.nus.edu.sg.mygo.models

import iss.nus.edu.sg.mygo.enum.TicketType

data class Attraction(
    val uuid: String,
    val name: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val description: String,
    val price: String,
    val openTime: String,
    val ticketAvailability: Boolean,
    var imageUuid: String // ✅ 只存 UUID，不存 URL
)

