package iss.nus.edu.sg.mygo.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.api.service.AccommodationApiService
import iss.nus.edu.sg.mygo.api.models.AccommodationResponse
import iss.nus.edu.sg.mygo.api.models.HotelBookingRequest
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.sessions.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.Date
import java.util.Locale
import iss.nus.edu.sg.mygo.adapter.ReviewAdapter
import iss.nus.edu.sg.mygo.models.Review
import iss.nus.edu.sg.mygo.models.ReviewStatus
import kotlinx.coroutines.withContext
import iss.nus.edu.sg.mygo.databinding.ActivityHotelDetailBinding


/**
 * @ClassName HotelDetailActivity
 * @Description
 * @Author YAO YIYANG
 * @StudentID A0294873L
 * @Date 2025/2/11
 * @Version 1.0
 */

class HotelDetailActivity : AppCompatActivity() {

    private lateinit var hotelNameTextView: TextView
    private lateinit var hotelDescriptionTextView: TextView
    private lateinit var hotelImageView: ImageView
    private lateinit var hotelAddressTextView: TextView
    private lateinit var wordListContainer: LinearLayout
    private lateinit var hotelPriceTextView: TextView
    private lateinit var hotelRatingTextView: TextView
    private lateinit var nearestMrtTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var websiteTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var companyTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var temporarilyClosedTextView: TextView
    private lateinit var binding: ActivityHotelDetailBinding

    private lateinit var userApiService: UserApiService
    private  lateinit var sessionManager: SessionManager
    private var leadInPrice: String? = null
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviews = mutableListOf<Review>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // 启用全屏显示
        setContentView(R.layout.activity_hotel_detail)
        binding = ActivityHotelDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 处理窗口的系统栏（状态栏、导航栏）内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_hoteldetail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 获取 UI 组件
        hotelNameTextView = findViewById(R.id.txt_hotel_name)
        hotelDescriptionTextView = findViewById(R.id.txt_product_info_description)
        hotelImageView = findViewById(R.id.container_product_image)
        hotelAddressTextView = findViewById(R.id.txt_hotel_address)
        wordListContainer = findViewById(R.id.word_list_container)
        hotelPriceTextView = findViewById(R.id.txt_price_value)
        hotelRatingTextView = findViewById(R.id.txt_review_count)

        nearestMrtTextView = findViewById(R.id.txt_mrt)
        emailTextView = findViewById(R.id.txt_email)
        websiteTextView = findViewById(R.id.txt_website)
        phoneTextView = findViewById(R.id.txt_phone)
        companyTextView = findViewById(R.id.txt_company)
        typeTextView = findViewById(R.id.txt_type)
        temporarilyClosedTextView = findViewById(R.id.txt_temporarily_closed)
        hotelRatingTextView = findViewById(R.id.txt_review_count)

        sessionManager = SessionManager(this)
        userApiService = UserApiService.create()

        // 设置返回按钮的点击事件
        val backButton: ImageButton = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()  // 关闭当前 Activity，返回到 HotelMainActivity
        }

        // 添加预订按钮点击事件
        val bookButton: LinearLayout = findViewById(R.id.container_cta)
        // 设置点击事件显示日历
        bookButton.setOnClickListener {
            // todo check user login?
            if(!sessionManager.isLoggedIn()){
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("from_activity", true) // 让 LoginActivity 知道是从哪里来的
                startActivityForResult(intent, 1001)

            }else {
                // 弹出日期选择器
                showCheckInDatePicker()
            }
        }


        // 获取从 Intent 传递的 UUID
        setupRecyclerView()
        val hotelUuid = intent.getStringExtra("hotel_uuid")
        Log.d("HotelDetail", "Fetching reviews for hotelUuid: $hotelUuid")
        if (hotelUuid != null) {
            fetchHotelDetails(hotelUuid)
            fetchReviews(hotelUuid)
        }
    }

    private fun fetchHotelDetails(uuid: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.stb.gov.sg/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(AccommodationApiService::class.java)
        val apiKey = "pRUJyzyyzpRd557ynCs7JRZtoKYr6PPC"

        val call = apiService.searchAccommodationByUUID(apiKey, "en", "uuids", uuid)
        call.enqueue(object : Callback<AccommodationResponse> {
            override fun onResponse(call: Call<AccommodationResponse>, response: Response<AccommodationResponse>) {
                if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
                    val hotelData = response.body()?.data?.firstOrNull()
                    if (hotelData != null) {
                        runOnUiThread {
                            hotelNameTextView.text = hotelData.name
                            hotelDescriptionTextView.text = hotelData.description
                            hotelRatingTextView.text = if (hotelData.rating == 0.0) "No Rating" else hotelData.rating.toString()
                            hotelAddressTextView.text = listOfNotNull(
                                hotelData.address.block,
                                hotelData.address.streetName,
                                hotelData.address.floorNumber?.let { floor ->
                                    hotelData.address.unitNumber?.let { unit -> "$floor-$unit" } ?: floor
                                },
                                hotelData.address.buildingName,
                                hotelData.address.postalCode
                            ).joinToString(" ").ifEmpty { "Unknown Location" }
                            leadInPrice = hotelData.leadInRoomRates
                            hotelPriceTextView.text = leadInPrice ?: "Price not available"

                            nearestMrtTextView.text = hotelData.nearestMrtStation ?: "Nothing"
                            emailTextView.text = hotelData.officialEmail ?: "Nothing"
                            websiteTextView.text = hotelData.officialWebsite ?: "Nothing"
                            phoneTextView.text = hotelData.contact?.primaryContactNo ?: "Nothing"
                            companyTextView.text = hotelData.companyDisplayName ?: "Nothing"
                            typeTextView.text = hotelData.type ?: "Nothing"
                            temporarilyClosedTextView.text = hotelData.temporarilyClosed ?: "Nothing"


                            // 加载景点图片，最多加载 5 张
                            val imageUrls = hotelData.images?.take(5)?.map { image ->
                                "http://10.0.2.2:8080/proxy/media/${image.uuid}"
                            } ?: emptyList()

                            // 设置图片切换功能
                            startImageSlideshow(imageUrls)

                            // 清空已有的设施列表，防止重复添加
                            wordListContainer.removeAllViews()

                            // 添加 "Facilities" 标题
                            val facilitiesTitle = TextView(this@HotelDetailActivity).apply {
                                text = "Facilities"
                                textSize = 18f
                                setTextColor(Color.parseColor("#FF232323"))
                                setTypeface(null, Typeface.BOLD)
                            }
                            wordListContainer.addView(facilitiesTitle)

                            // 解析设施列表并动态添加
                            hotelData.amenities?.split(",")?.map { it.trim() }?.forEach { amenity ->
                                val wordTextView = TextView(this@HotelDetailActivity).apply {
                                    text = "· $amenity"
                                    textSize = 16f
                                    setTextColor(Color.BLACK)
                                }
                                this@HotelDetailActivity.wordListContainer.addView(wordTextView)
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<AccommodationResponse>, t: Throwable) {
                Toast.makeText(this@HotelDetailActivity, "Failed to load hotel details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private var currentImageIndex = 0
    private var imageUrls: List<String> = emptyList()

    // image switching
    private val handler = Handler(Looper.getMainLooper()) // ✅ 只用一个 handler 进行管理

    private fun startImageSlideshow(urls: List<String>) {
        imageUrls = urls
        if (imageUrls.isNotEmpty()) {
            if (!isDestroyed && !isFinishing) { // ✅ 确保 Activity 仍然存活
                Glide.with(this)
                    .load(imageUrls[currentImageIndex])
                    .placeholder(R.drawable.attraction_placeholder_image)
                    .error(R.drawable.attraction_placeholder_image)
                    .into(hotelImageView)
            }

            // 启动定时器切换图片
            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (!isDestroyed && !isFinishing && imageUrls.isNotEmpty()) { // ✅ Activity 未销毁时才运行
                        currentImageIndex = (currentImageIndex + 1) % imageUrls.size
                        Glide.with(this@HotelDetailActivity)
                            .load(imageUrls[currentImageIndex])
                            .placeholder(R.drawable.attraction_placeholder_image)
                            .error(R.drawable.attraction_placeholder_image)
                            .into(hotelImageView)

                        handler.postDelayed(this, 2000) // ✅ 继续切换
                    }
                }
            }, 2000) // ✅ 启动轮播
        }
    }

    private fun showCheckInDatePicker() {
        val datePicker = CalendarView(this)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select check in date:")
        builder.setView(datePicker)

        datePicker.minDate = System.currentTimeMillis() // 不能选择过去的日期

        var selectedCheckInDate = System.currentTimeMillis() // 记录选择的入住日期

        datePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedCheckInDate = parseDateToTimestamp(year, month, dayOfMonth)
        }

        builder.setPositiveButton("Next") { _, _ ->
            showCheckOutDatePicker(selectedCheckInDate)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun showCheckOutDatePicker(checkInDate: Long) {
        val datePicker = CalendarView(this)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select check out date:")
        builder.setView(datePicker)

        datePicker.minDate = checkInDate + (24 * 60 * 60 * 1000) // 退房日期必须大于入住日期

        var selectedCheckOutDate = checkInDate + (24 * 60 * 60 * 1000)

        datePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedCheckOutDate = parseDateToTimestamp(year, month, dayOfMonth)
        }

        builder.setPositiveButton("Next") { _, _ ->
            showRoomTypeSelection(checkInDate, selectedCheckOutDate)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun showRoomTypeSelection(checkInDate: Long, checkOutDate: Long) {
        val roomTypes = arrayOf("Standard", "Supreme", "Suite")
        var selectedRoomType = roomTypes[0] // 默认选择标准房

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select room type:")

        builder.setSingleChoiceItems(roomTypes, 0) { _, which ->
            selectedRoomType = roomTypes[which]
        }

        builder.setPositiveButton("Next") { _, _ ->
            val calculatedPrice = calculatePrice(selectedRoomType)
            Toast.makeText(this, "Selected ${selectedRoomType}, Price: $calculatedPrice", Toast.LENGTH_LONG).show()
            showGuestNumberInput(checkInDate, checkOutDate, selectedRoomType, calculatedPrice)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun showGuestNumberInput(checkInDate: Long, checkOutDate: Long, roomType: String, calculatedPrice: String) {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Input number of customer"

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Number of customer")
        builder.setView(input)

        builder.setPositiveButton("Confirm booking") { _, _ ->
            val guestsInput = input.text.toString()
            if (guestsInput.isBlank()) {
                Toast.makeText(this, "Please input correct number:", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            val guests = guestsInput.toIntOrNull() ?: 1
            sendBookingRequest(checkInDate, checkOutDate, roomType, guests, calculatedPrice)
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun sendBookingRequest(checkInDate: Long, checkOutDate: Long, roomType: String, guests: Int, calculatedPrice: String) {
        val userId = getUserId() ?: run {
            Toast.makeText(this, "Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        val hotelUuid = intent.getStringExtra("hotel_uuid") ?: run {
            Toast.makeText(this, "Lack of information of Hotel", Toast.LENGTH_SHORT).show()
            return
        }

        val formattedCheckIn = formatDateToBackendFormat(checkInDate)
        val formattedCheckOut = formatDateToBackendFormat(checkOutDate)

        val request = HotelBookingRequest(
            uuid = hotelUuid,
            userId = userId.toInt(),
            checkInDate = formattedCheckIn,
            checkOutDate = formattedCheckOut,
            roomType = roomType,
            guests = guests,
            price = calculatedPrice
        )

        println("📌 Booking Request: $request")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userApiService.createHotelBooking(request)

                if (response.isSuccessful) {
                    val bookingResponse = response.body()
                    println("✅ Booking Succeed: $bookingResponse")

                    runOnUiThread {
                        Toast.makeText(this@HotelDetailActivity, "Booking Successfully！", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("🚨 Booking failed: ${response.code()} - $errorBody")

                    runOnUiThread {
                        Toast.makeText(this@HotelDetailActivity, "Booking failed: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                println("🚨 Booking exception: ${e.message}")

                runOnUiThread {
                    Toast.makeText(this@HotelDetailActivity, "NetWork Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun calculatePrice(roomType: String): String {
        leadInPrice?.let {
            val priceNumbers = it.replace("S$", "").split("-").map { price -> price.trim().toIntOrNull() }
            return when {
                priceNumbers.size == 1 -> "${priceNumbers[0]}" // 单个价格
                priceNumbers.size == 2 && priceNumbers[0] != null && priceNumbers[1] != null -> {
                    val minPrice = priceNumbers[0]!!
                    val maxPrice = priceNumbers[1]!!
                    when (roomType) {
                        "Standard" -> "$minPrice"
                        "Supreme" -> "${(minPrice + maxPrice) / 2}"
                        "Suite"  -> "$maxPrice"
                        else -> "Price cannot adapt"
                    }
                }
                else -> "Price cannot adapt"
            }
        } ?: return "Price cannot adapt"
    }

    private fun formatDateToBackendFormat(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    private fun getUserId(): String? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", null) // 读取 userId
    }

    private fun parseDateToTimestamp(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0) // 设为 00:00:00 确保匹配
        calendar.set(Calendar.MILLISECOND, 0)

        val timestamp = calendar.timeInMillis

        // ✅ 调试打印，确保 selectedTime 正确
        val debugDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(timestamp)
        println("Selected Date Timestamp: $debugDate ($timestamp)")

        return timestamp
    }

    private fun fetchReviews(hotelUuid: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = userApiService.getHotelReviews(hotelUuid)
                Log.d("HotelDetail", "Raw Review Response: ${response.body()}")

                if (response.isSuccessful) {
                    val fetchedReviews = response.body()?.filter { it.status == ReviewStatus.SHOW } ?: emptyList()
                    withContext(Dispatchers.Main) {
                        if (fetchedReviews.isNotEmpty()) {
                            reviews.clear()
                            reviews.addAll(fetchedReviews)
                            reviewAdapter.notifyDataSetChanged()
                            binding.reviewSection.visibility = View.VISIBLE
                        } else {
                            binding.reviewSection.visibility = View.GONE
                        }
                    }
                } else {
                    Log.e("HotelDetail", "Failed to fetch reviews: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HotelDetail", "Error fetching reviews", e)
            }
        }
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(reviews)
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reviewRecyclerView.adapter = reviewAdapter
    }



    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // ✅ 防止定时任务继续执行
        Glide.with(applicationContext).clear(hotelImageView) // ✅ 清理 Glide 加载任务
    }
}