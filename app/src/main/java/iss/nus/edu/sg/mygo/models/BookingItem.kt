package iss.nus.edu.sg.mygo.models
/**
 * @ClassName BookingItem
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/2/4
 * @Version 1.3
 */
sealed class BookingItem {
    data class AttractionBookingItem(val attractionBooking: AttractionBooking) : BookingItem()
    data class HotelBookingItem(val hotelBooking: HotelBooking) : BookingItem()
    data class FlightBookingItem(val filghtBooking: FlightBookingRequest) : BookingItem()
}
