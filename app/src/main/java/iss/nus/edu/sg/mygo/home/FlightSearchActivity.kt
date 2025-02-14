package iss.nus.edu.sg.mygo.home

/**
 * @ClassName FlightSearchActivity
 * @Description
 * @Author Siti Alifah Binte Yahya
 * @StudentID A0295324B
 * @Date 13 Feb 2025
 * @Version 1.0
 */


import iss.nus.edu.sg.mygo.R
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.adapter.FlightAdapter
import androidx.activity.viewModels
import iss.nus.edu.sg.mygo.viewmodel.flightViewModel
import java.util.Calendar

/*Purpose
*handling the flight search feature,
*Reads user input from the layout
*Calls the backend via ViewModel
*Displays flight results in RecyclerView
 */

/*Core components
*ViewModel: Manages search logic and API calls
*Adapter: Displays results
* LiveData: Observes and updates UI automatically
* */

class FlightSearchActivity : AppCompatActivity() {

    private lateinit var departureEditText: EditText
    private lateinit var destinationEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var spinnerPassengers: Spinner
    private lateinit var spinnerClass: Spinner
    private lateinit var searchButton: Button
    private lateinit var flightRecyclerView: RecyclerView
    private lateinit var noResultsText: TextView
    private lateinit var progressBar: ProgressBar

    private val flightViewModel: flightViewModel by viewModels()
    private lateinit var flightAdapter: FlightAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_search)

        // Initialize UI Components
        departureEditText = findViewById(R.id.editTextDeparture)
        destinationEditText = findViewById(R.id.editTextArrival)
        dateEditText = findViewById(R.id.tvDateSelection)
        spinnerPassengers = findViewById(R.id.spinnerPassengers)
        spinnerClass = findViewById(R.id.spinnerClass)
        searchButton = findViewById(R.id.btnSearchFlights)
        flightRecyclerView = findViewById(R.id.recyclerView_FlightResults)
        noResultsText = findViewById(R.id.noResultsText)
        progressBar = findViewById(R.id.progressBar)

        // Initialize FlightAdapter
        flightAdapter = FlightAdapter { flight ->
            Toast.makeText(this, "Booking ${flight.airlineName}", Toast.LENGTH_SHORT).show()
        }


        flightRecyclerView.layoutManager = LinearLayoutManager(this)
        flightRecyclerView.adapter =
            flightAdapter // Assigns an adapter to display flight search results.

        // Setup Passengers Spinner
        val passengerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.passenger_options,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerPassengers.adapter = passengerAdapter

        // Setup Class Spinner
        val classAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.class_options,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerClass.adapter = classAdapter

        dateEditText.setOnClickListener {
            showDatePicker()
        }

        searchButton.setOnClickListener {
            searchFlights()
        }

        // Initialize RecyclerView
        //flightAdapter = FlightAdapter(this) { flight ->
            //Toast.makeText(this, "Booking ${flight.airlineName}", Toast.LENGTH_SHORT).show()
        //}



        // Hide results section initially
        progressBar.visibility = View.VISIBLE
        flightRecyclerView.visibility = View.GONE
        noResultsText.visibility = View.GONE

        // Observe ViewModel for flight results and update RecyclerView
        flightViewModel.flights.observe(this) { flights ->
            progressBar.visibility = View.GONE
            if (flights.isNotEmpty()) {
                flightRecyclerView.visibility = View.VISIBLE
                noResultsText.visibility = View.GONE
                flightAdapter.submitList(flights)
            } else {
                flightRecyclerView.visibility = View.VISIBLE
                noResultsText.visibility = View.GONE
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = android.app.DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = "$year-${month + 1}-$dayOfMonth"
                dateEditText.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    //Performing API Calls and  Retrieves user input from the text fields.
    private fun searchFlights() {
        // collect input from UI
        val departure = departureEditText.text.toString().trim()
        val destination = destinationEditText.text.toString().trim()
        val date = dateEditText.text.toString().trim()
        val passengers = spinnerPassengers.selectedItem.toString().toInt()

        if (departure.isBlank() || destination.isBlank() || date.isBlank()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading indicator
        progressBar.visibility = View.VISIBLE
        flightRecyclerView.visibility = View.GONE
        noResultsText.visibility = View.GONE

        // Call ViewModel to fetch flights
        flightViewModel.searchFlights(departure, destination, date, passengers)

    }
}