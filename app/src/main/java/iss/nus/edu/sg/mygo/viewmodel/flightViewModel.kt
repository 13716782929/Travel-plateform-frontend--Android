package iss.nus.edu.sg.mygo.viewmodel
/**
 * @ClassName flightViewModel
 * @Description
 * @Author Siti Alifah Binte Yahya
 * @StudentID A0295324B
 * @Date 13 Feb 2025
 * @Version 1.0
 */
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import iss.nus.edu.sg.mygo.api.models.FlightSearchRequest
import iss.nus.edu.sg.mygo.api.service.FlightApiService
import iss.nus.edu.sg.mygo.models.FlightInfo
import kotlinx.coroutines.launch

// purpose: Acts as the bridge between the UI and backend, fetching flights and holding data for the Activity.

/**Core components
 *LiveData to store and observe flight results
 * MutableLiveData to update results
 * Coroutines to call the backend on a background thread
 * */
class flightViewModel : ViewModel() {

    // Hold Search Results:
    private val _flights = MutableLiveData<List<FlightInfo>>()
    val flights: LiveData<List<FlightInfo>> get() = _flights

    // Create an instance of FlightApiService
    private val flightApiService = FlightApiService.create()

    //Call Backend API
    //viewModelScope.launch to run coroutines for API calls
    fun searchFlights(departure: String, destination: String, date: String, passengers: Int) {
        viewModelScope.launch {
            try {
                // Use proper request object
                val request = FlightSearchRequest(
                    from = departure,
                    to = destination,
                    date = date,
                    passengers = passengers
                )
                // Call the API using the instance, not the class name
                val response = flightApiService.getFlights(request)
                _flights.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
                _flights.postValue(emptyList()) // Show empty results on error
            }
        }
    }
}