package iss.nus.edu.sg.mygo.models

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
    val attractionImageUuid: String
)

