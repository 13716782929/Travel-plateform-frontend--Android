package iss.nus.edu.sg.mygo.models

sealed class BookingItem {
    data class AttractionBookingItem(val attractionBooking: AttractionBooking) : BookingItem()
    data class HotelBookingItem(val hotelBooking: HotelBooking) : BookingItem()
    data class FlightBookingItem(val filghtBooking: FlightBookingRequest) : BookingItem()
}
