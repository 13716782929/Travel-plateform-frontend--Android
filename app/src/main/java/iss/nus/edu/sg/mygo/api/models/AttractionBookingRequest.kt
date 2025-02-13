package iss.nus.edu.sg.mygo.api.models

/**
 * @ClassName AttractionBookingRequest
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/2/5
 * @Version 1.3
 */


import com.google.gson.annotations.SerializedName

data class AttractionBookingRequest(
    @SerializedName("uuid") val uuid: String, // 景点 UUID
    @SerializedName("userId") val userId: String, // 用户 ID
    @SerializedName("visitDate") val visitDate: String, // 访问日期 (格式: yyyy-MM-dd)
    @SerializedName("visitTime") val visitTime: String, // 访问时间 (格式: HH:mm)
    @SerializedName("numberOfTickets") val numberOfTickets: Int, // 票数
    @SerializedName("price") val price: String // 总价格 (String 格式)
)
