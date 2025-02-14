package iss.nus.edu.sg.mygo.api.models

/**
 * @ClassName FlightSearchRequest
 * @Description
 * @Author Siti Alifah Binte Yahya
 * @StudentID A0295324B
 * @Date 13 Feb 2025
 * @Version 1.0
 */

import com.google.gson.annotations.SerializedName

data class FlightSearchRequest(
    @SerializedName("from") val from: String,
    @SerializedName("to") val to: String,
    @SerializedName("date") val date: String,
    @SerializedName("passengers") val passengers: Int
)
