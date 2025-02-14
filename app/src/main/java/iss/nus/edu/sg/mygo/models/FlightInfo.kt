package iss.nus.edu.sg.mygo.models

import kotlin.time.Duration

/**
 * @ClassName FlightInfo
 * @Description
 * @Author Wang Chang and Siti Alifah Binte Yahya
 * @StudentID A0295324B
 * @Date 13 Feb 2025
 * @Version 1.0
 */

//data class for response
data class FlightInfo(
    val airlineId: String?,
    val airlineName: String?,
    val departureLocation: String?,
    val arrivalLocation: String?,
    val flightDate: String?,
    val departureTime: String?,
    val arrivalTime: String?,
    val duration: String,
    val price: String,

)
// simulate fake data of flight info
