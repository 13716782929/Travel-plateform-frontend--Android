package iss.nus.edu.sg.mygo.models

import iss.nus.edu.sg.mygo.enum.TicketType

/*
Class name:
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date: 6 Feb 2025
Version
*/

data class Attraction(
    val uuid: String,
    val name: String,
    val address: String,
    val description: String,
    val rate: Double,
    val price: String,
//    val openTime: String,
    var imageUuid: String // ✅ 只存 UUID，不存 URL
)

