package iss.nus.edu.sg.mygo.api.models

import java.math.BigDecimal

/**
 * @ClassName ReviewRequest
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/2/11
 * @Version 1.3
 */

data class ReviewRequest(
    val userId: Int,        // 用户 ID
    val itemType: String,   // "Attraction" / "Hotel" / "Flight"
    val itemId: Int,        // 关联的景点、酒店或航班 ID
    val bookingId: Int,     // 关联的 Booking ID
    val rating: BigDecimal, // 评分 (1.0 - 5.0)
    val comment: String     // 用户评论内容
)