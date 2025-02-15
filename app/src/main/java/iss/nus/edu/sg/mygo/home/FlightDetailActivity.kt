package iss.nus.edu.sg.mygo.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.api.service.FlightApiService
import iss.nus.edu.sg.mygo.home.FlightPaymentActivity
import iss.nus.edu.sg.mygo.models.Flight
import iss.nus.edu.sg.mygo.models.FlightBooking
import iss.nus.edu.sg.mygo.models.FlightBookingRequest
import iss.nus.edu.sg.mygo.sessions.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    private lateinit var durationDate: TextView
    private lateinit var backButton: ImageButton
    private  lateinit var sessionManager: SessionManager

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
        durationDate = findViewById(R.id.detail_flight_on_date)
        departureTimeTextView = findViewById(R.id.fligh_detail_departure_time)
        departureCityTextView = findViewById(R.id.fligh_detail_departure_city)
        departureAirportTextView = findViewById(R.id.fligh_detail_departure_airport)
        arrivalTimeTextView = findViewById(R.id.fligh_detail_arrival_time)
        arrivalCityDetailTextView = findViewById(R.id.fligh_detail_arrival_city)
        arrivalAirportTextView = findViewById(R.id.fligh_detail_arrival_airport)
        priceTextView = findViewById(R.id.txt_price_value)
        bookTextView = findViewById(R.id.txt_cta_book_now)
        backButton = findViewById(R.id.button_back)

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
        sessionManager = SessionManager(this)

        bookTextView.setOnClickListener {
            if(!sessionManager.isLoggedIn()){
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("from_activity", true) // 让 LoginActivity 知道是从哪里来的
                startActivityForResult(intent, 1001)

            }
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
        // 解析日期时间
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm") // 只提取时间
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 只提取日期

        val departureDateTime = LocalDateTime.parse(flight.departureTime, formatter)
        val arrivalDateTime = LocalDateTime.parse(flight.arrivalTime, formatter)

        val departureTime = departureDateTime.format(timeFormatter) // "22:17"
        val arrivalTime = arrivalDateTime.format(timeFormatter) // "11:17"

        val departureDate = departureDateTime.format(dateFormatter) // "2025-03-07"
        val arrivalDate = arrivalDateTime.format(dateFormatter) // "2025-03-08"

        // 设置 UI
        titleTextView.text = "Flight Detail"
        arrivalCityTextView.text = "To: ${flight.arrivalCity}"
        airlineTextView.text = flight.airline
        durationTextView.text = flight.duration
        statusTextView.text = flight.flightStatus

        durationDate.text = "$departureDate → $arrivalDate" // 设置日期格式
        departureTimeTextView.text = departureTime // 只显示时间
        arrivalTimeTextView.text = arrivalTime // 只显示时间

        departureCityTextView.text = flight.departureCity
        departureAirportTextView.text = flight.departureAirport
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
        val seatTypes = arrayOf("First", "Business", "Economy")
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
    private fun bookFlight(seatClass: String, passengerCount: Int, totalPrice: Double) {
        val userId = getUserId()
        if (userId == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_LONG).show()
            return
        }

        val selectedSeats = generateRandomSeats(passengerCount)

        val request = FlightBookingRequest(
            userId = userId,
            selectedSeats = seatClass,
            id = flightId,
            type = "Flight",
            totalPrice = totalPrice
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = flightApiService.bookFlight(request)
                Log.e("BookingFlightRequest","Request =${request}")

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val bookingResponse = response.body()

                        // ✅ 预订成功后跳转到 FlightPaymentActivity，并传递 totalAmount
                        val intent = Intent(this@FlightDetailActivity, FlightPaymentActivity::class.java)
                        intent.putExtra("totalPrice", totalPrice)
                        startActivity(intent)

                        Log.d("BOOKING_SUCCESS", "Booking Successful: $bookingResponse")
                        Toast.makeText(this@FlightDetailActivity, "Booking Successful!", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("BOOKING_ERROR", "Response Code: ${response.code()}, Error Body: $errorBody")
                        Toast.makeText(this@FlightDetailActivity, "Booking Failed: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("BOOKING_EXCEPTION", "Network Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FlightDetailActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    /**
     * get UserId from sharedPrefs
     */
    private fun getUserId(): Long? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userIdString = sharedPreferences.getString("user_id", null)
        Log.e("UserId","${userIdString}")
        Log.e("UserId","${userIdString?.toLongOrNull()}")

        return userIdString?.toLongOrNull() // 如果解析失败，返回 -1
    }

    /**
     * generate random seats based on passengerNum
     */
    private fun generateRandomSeats(passengerCount: Int): String {
        val rows = (1..30).toList() // 1-30 排
        val seats = listOf("A", "B", "C", "D", "E", "F") // 6 个座位
        val assignedSeats = mutableSetOf<String>() // ✅ 用 Set 避免重复

        while (assignedSeats.size < passengerCount) {
            val row = rows.random()
            val seat = seats.random()
            assignedSeats.add("$row$seat") // ✅ 确保唯一性
        }

        return assignedSeats.joinToString(",") // ✅ 生成 "3A,4B" 格式
    }
}
