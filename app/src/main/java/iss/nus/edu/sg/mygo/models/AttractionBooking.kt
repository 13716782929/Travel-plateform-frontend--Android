package iss.nus.edu.sg.mygo.models

/**
 * @ClassName AttractionBooking
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/1/7
 * @Version 1.3
 */

data class AttractionBooking(
    val attractionBookingId: Int,
    val bookingId: Int,
    val attractionId: Int,
    val attractionUuid: String?, // nullable，后端可能找不到 UUID
    // about to show
    val attractionName: String,
    val location: String,
    val visitDate: String,
    val visitTime: String,
    val numberOfTickets: Int,
    val attractionImageUuid: String,
    val status: String
)

