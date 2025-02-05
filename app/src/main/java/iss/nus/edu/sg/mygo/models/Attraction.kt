package iss.nus.edu.sg.mygo.models

import iss.nus.edu.sg.mygo.enum.TicketType

data class Attraction(
    val uuid: String,
    val attractionName: String,
    val location: String,
    val description: String,
    val openTime: String,
    val ticketAvailability: Boolean,
    val ticketType: TicketType,
    val imageResId: Int
)
