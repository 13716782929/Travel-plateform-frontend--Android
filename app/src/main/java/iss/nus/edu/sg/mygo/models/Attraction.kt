package iss.nus.edu.sg.mygo.models

import iss.nus.edu.sg.mygo.enum.TicketType

data class Attraction(
    val uuid: String,
    val name: String,
    val address: String,              // 格式化后的地址（例如 "1 Example St, Singapore 123456"）
    val latitude: Double?,            // 纬度
    val longitude: Double?,           // 经度
    val description: String,
    val price: String,                // 格式化之后的价格
    val openTime: String,             // 格式化后的开放时间（如 "Daily: 00:00-23:59"）
    val ticketAvailability: Boolean,  // 是否有票
    var imageUrls: List<String>       // 改为存储网络图片的 URL 列表
)
