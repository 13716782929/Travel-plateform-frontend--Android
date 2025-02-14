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

import com.google.gson.annotations.SerializedName

data class FlightInfo(
    @SerializedName("flightId") val flightId: Int?, // 航班唯一标识
    @SerializedName("flightNumber") val flightNumber: String?, // 航班号
    @SerializedName("airline") val airlineName: String?, // 航空公司
    @SerializedName("departureCity") val departureCity: String?, // 出发城市
    @SerializedName("arrivalCity") val arrivalCity: String?, // 到达城市
    @SerializedName("departureAirport") val departureAirport: String?, // 出发机场
    @SerializedName("arrivalAirport") val arrivalAirport: String?, // 到达机场
    @SerializedName("departureTime") val departureTime: String?, // 出发时间
    @SerializedName("arrivalTime") val arrivalTime: String?, // 到达时间
    @SerializedName("duration") val duration: String?, // 航程时长
    @SerializedName("seatAvailability") val seatAvailability: Map<String, Int>?, // 舱位剩余座位
    @SerializedName("seatType") val seatType: Map<String, String>?, // 座位类型
    @SerializedName("flightStatus") val flightStatus: String?, // 航班状态
    @SerializedName("lastUpdated") val lastUpdated: String? // 更新时间
)
