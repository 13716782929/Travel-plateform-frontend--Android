package iss.nus.edu.sg.mygo.models

data class FlightBookingRequest(
    val userId: Long,       // 用户 ID
    val selectedSeats: String, // 选择的座位号（JSON 字符串）
    val id: Int,            // 航班 ID
    val type: String,       // 预订类型，需要搞清楚type是指什么？目前传入的是seatType，后端没找到type的调用方式
    val totalPrice: Double  // 订单总价
)
