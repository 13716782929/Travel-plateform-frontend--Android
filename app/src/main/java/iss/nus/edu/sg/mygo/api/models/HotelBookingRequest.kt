package iss.nus.edu.sg.mygo.api.models

import com.google.gson.annotations.SerializedName

data class HotelBookingRequest(
    @SerializedName("uuid") val uuid: String, // 景点 UUID
    @SerializedName("userId") val userId: Int, // 用户 ID
    @SerializedName("checkInDate") val checkInDate: String, // 访问日期 (格式: yyyy-MM-dd)
    @SerializedName("checkOutDate") val checkOutDate: String, // 访问时间 (格式: HH:mm)
    @SerializedName("roomType") val roomType: String, // 票数
    @SerializedName("guests") val guests: Int, // 票数
    @SerializedName("price") val price: String // 总价格 (String 格式)
)