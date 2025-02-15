package iss.nus.edu.sg.mygo.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import iss.nus.edu.sg.mygo.api.service.FlightApiService
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModelProvider
import retrofit2.await

class FlightTicketViewModel(private val flightApiService: FlightApiService) : ViewModel() {

    private val _flightBookings = MutableStateFlow<List<FlightBooking>>(emptyList())
    val flightBookings: StateFlow<List<FlightBooking>> = _flightBookings

    private val _flights = MutableStateFlow<List<Flight>>(emptyList())
    val flights: StateFlow<List<Flight>> = _flights

    // ✅ 获取用户预订的航班
    fun getUserFlightBookings(userId: Int) {
        viewModelScope.launch {
            try {
                val bookings = flightApiService.getFlightsByUserId(userId)
                _flightBookings.value = bookings
                fetchFlightDetails(bookings)
            } catch (e: Exception) {
                Log.e("FlightTicketViewModel","Bookings: ${_flightBookings.value}")
                e.printStackTrace()
            }
        }
    }

    // ✅ 根据 `flightBooking` 获取 `Flight` 详情
    private suspend fun fetchFlightDetails(bookings: List<FlightBooking>) {
        val flightsList = mutableListOf<Flight>()
        for (booking in bookings) {
            Log.d("FlightBooking","${booking}")
            booking.flightId?.let { flightId ->
                try {
                    val response = flightApiService.getFlightById(flightId).await()
                    Log.d("FlightBookingResponse","${response}")
                    flightsList.add(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        _flights.value = flightsList
    }

    // ✅ `ViewModelProvider.Factory` 用于在 `HomeFragment` 里创建 `ViewModel`
    class FlightViewModelFactory(private val flightApiService: FlightApiService) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FlightTicketViewModel::class.java)) {
                return FlightTicketViewModel(flightApiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
