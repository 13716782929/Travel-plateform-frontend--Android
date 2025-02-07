package iss.nus.edu.sg.mygo.models

/*
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date: 7/2/2025
*/

import iss.nus.edu.sg.mygo.enum.TicketType

data class AttractionImageResponse(
    val uuid: String,
    val attractionName: String,
    val location: String,
    val description: String,
    val openTime: String,
    val ticketAvailability: Boolean,
    val ticketType: TicketType,
    val imageResId: Int
)
