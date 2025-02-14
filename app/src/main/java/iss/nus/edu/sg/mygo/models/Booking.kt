package iss.nus.edu.sg.mygo.models

/**
 * @ClassName Booking
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/1/25
 * @Version 1.3
 */

data class Booking(
    val bookingId: String,
    val bookingType: String,
    val status: String,
    val userId: String
)
