package iss.nus.edu.sg.mygo.models
/**
 * @ClassName FlightBooking
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/1/25
 * @Version 1.3
 */
data class FlightBooking(
    val flightBookingId: Int, // 预订唯一标识
    val bookingId: Int?,  // 预订 ID，可能为 null
    val flightId: Int?,   // 航班 ID
    val seatClass: String?, // 座位等级 (Economy, Business, First)
    val passengerName: String?, // 乘客姓名
    val passengerId: String? // 乘客证件号
)
