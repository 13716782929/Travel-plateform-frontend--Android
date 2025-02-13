package iss.nus.edu.sg.mygo.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.api.service.FlightApiService
import iss.nus.edu.sg.mygo.home.FlightPaymentActivity
import iss.nus.edu.sg.mygo.models.Flight
import iss.nus.edu.sg.mygo.models.FlightBookingRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class FlightDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var arrivalCityTextView: TextView
    private lateinit var airlineTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var departureTimeTextView: TextView
    private lateinit var departureCityTextView: TextView
    private lateinit var departureAirportTextView: TextView
    private lateinit var arrivalTimeTextView: TextView
    private lateinit var arrivalCityDetailTextView: TextView
    private lateinit var arrivalAirportTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var bookTextView: TextView

    private var flightId: Int = -1
    private val flightApiService = FlightApiService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_detail)

        // 初始化 UI
        titleTextView = findViewById(R.id.flight_detail_title)
        arrivalCityTextView = findViewById(R.id.detail_flight_txt_arrivalCity)
        airlineTextView = findViewById(R.id.detail_flight_txt_airline)
        durationTextView = findViewById(R.id.detail_flight_txt_duration)
        statusTextView = findViewById(R.id.detail_flight_txt_status)
        departureTimeTextView = findViewById(R.id.fligh_detail_departure_time)
        departureCityTextView = findViewById(R.id.fligh_detail_departure_city)
        departureAirportTextView = findViewById(R.id.fligh_detail_departure_airport)
        arrivalTimeTextView = findViewById(R.id.fligh_detail_arrival_time)
        arrivalCityDetailTextView = findViewById(R.id.fligh_detail_arrival_city)
        arrivalAirportTextView = findViewById(R.id.fligh_detail_arrival_airport)
        priceTextView = findViewById(R.id.txt_price_value)
        bookTextView = findViewById(R.id.txt_cta_book_now)

        // 获取上一个页面传递的 flightId
//        flightId = intent.getStringExtra("flightId")?.toIntOrNull() ?: -1
        flightId = 1

        if (flightId != -1) {
            fetchFlightDetails(flightId)
            generateRandomPrice()
        } else {
            Toast.makeText(this, "无效的 Flight ID", Toast.LENGTH_LONG).show()
            finish()
        }

        bookTextView.setOnClickListener {
            showBookingDialog()
        }
    }

    /**
     * 通过 API 获取航班详情
     */
    private fun fetchFlightDetails(flightId: Int) {
        flightApiService.getFlightById(flightId).enqueue(object : Callback<Flight> {
            override fun onResponse(call: Call<Flight>, response: Response<Flight>) {
                Log.e("FlightDetailActivity","${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let { flight ->
                        updateUI(flight)
                    }
                } else {
                    Log.e("FlightDetailActivity","${response}")
                    Toast.makeText(this@FlightDetailActivity, "获取航班信息失败", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Flight>, t: Throwable) {
                Toast.makeText(this@FlightDetailActivity, "网络错误: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * 将航班数据填充到 UI
     */
    private fun updateUI(flight: Flight) {
        titleTextView.text = "Flight Detail"
        arrivalCityTextView.text = "To: ${flight.arrivalCity}"
        airlineTextView.text = flight.airline
        durationTextView.text = flight.duration
        statusTextView.text = flight.flightStatus
        departureTimeTextView.text = flight.departureTime
        departureCityTextView.text = flight.departureCity
        departureAirportTextView.text = flight.departureAirport
        arrivalTimeTextView.text = flight.arrivalTime
        arrivalCityDetailTextView.text = flight.arrivalCity
        arrivalAirportTextView.text = flight.arrivalAirport
    }

    /**
     * 生成 500 - 1500 之间的随机价格
     */
    private fun generateRandomPrice() {
        val randomPrice = Random.nextDouble(200.00,1200.00)
        val formattedPrice = BigDecimal(randomPrice).setScale(2, RoundingMode.HALF_EVEN)
        priceTextView.text = "$$formattedPrice"
    }

    /**
     * 显示选择座位类型和购买数量的对话框
     */
    private fun showBookingDialog() {
        val seatTypes = arrayOf("First", "Suite", "Economy")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Please select seat type:")
            .setItems(seatTypes) { _, which ->
                val seatType = seatTypes[which]
                showSeatCountDialog(seatType)
            }
        builder.create().show()
    }

    /**
     * 显示选择购买数量的对话框
     */
    private fun showSeatCountDialog(seatType: String) {
        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Purchase Number:")
            .setView(input)
            .setPositiveButton("Confirm") { _, _ ->
                val count = input.text.toString().toIntOrNull() ?: 1
                val totalPrice = count * (priceTextView.text.toString().replace("$", "").toDouble())
                bookFlight(seatType, count, totalPrice)
            }
            .setNegativeButton("Cancel", null)
        builder.create().show()
    }

    /**
     * 调用 API 进行航班预订
     */
    private fun bookFlight(seatType: String, seatCount: Int, totalPrice: Double) {
        val request = FlightBookingRequest(
            userId = 1L,
            selectedSeats = "$seatType-$seatCount",
            id = flightId,
            type = seatType,
            totalPrice = totalPrice
        )

        flightApiService.bookFlight(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@FlightDetailActivity, FlightPaymentActivity::class.java)
                    intent.putExtra("totalPrice", totalPrice)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@FlightDetailActivity, "预订失败，请重试", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@FlightDetailActivity, "网络错误: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
