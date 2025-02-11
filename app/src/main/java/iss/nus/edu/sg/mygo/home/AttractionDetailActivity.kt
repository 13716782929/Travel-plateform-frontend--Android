package iss.nus.edu.sg.mygo.home

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.CalendarView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.api.service.AttractionApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import androidx.appcompat.app.AlertDialog
import iss.nus.edu.sg.mygo.api.models.AttractionBookingRequest
import iss.nus.edu.sg.mygo.api.models.BusinessHour
import iss.nus.edu.sg.mygo.api.service.UserApiService
import iss.nus.edu.sg.mygo.sessions.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar


class AttractionDetailActivity : AppCompatActivity() {

    private lateinit var attractionNameTextView: TextView
    private lateinit var attractionDescriptionTextView: TextView
    private lateinit var attractionImageView: ImageView
    private lateinit var attractionAddressTextView: TextView
    private lateinit var wordListContainer: LinearLayout
    private lateinit var attractionPriceTextView: TextView
    private lateinit var attractionRatingTextView: TextView
    private lateinit var apiService: AttractionApiService
    private lateinit var userApiService: UserApiService
    private lateinit var containerCta: LinearLayout
    private lateinit var bookButtonText: TextView
    private lateinit var bookButton: Button
    private  lateinit var sessionManager: SessionManager

    // 用于存储 BusinessHour 数据
    private var businessHours: List<BusinessHour> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // 启用全屏显示
        setContentView(R.layout.activity_attraction_detail)

        // 处理窗口的系统栏（状态栏、导航栏）内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_hoteldetail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 获取 UI 组件
        attractionNameTextView = findViewById(R.id.txt_hotel_name)
        attractionDescriptionTextView = findViewById(R.id.txt_attraction_info_description)
        attractionImageView = findViewById(R.id.container_attraction_image)
        attractionAddressTextView = findViewById(R.id.txt_attraction_address)
        wordListContainer = findViewById(R.id.word_list_container)
        attractionPriceTextView = findViewById(R.id.txt_price_value)
        attractionRatingTextView = findViewById(R.id.txt_review_count)
        // 获取 Calendar UI 组件
        containerCta = findViewById(R.id.container_cta)

        // 初始化 API Service
        apiService = AttractionApiService.create()
        userApiService = UserApiService.create()
        sessionManager = SessionManager(this)

        // 设置点击事件显示日历
        containerCta.setOnClickListener {
            // todo check user login?
            if(!sessionManager.isLoggedIn()){
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("from_activity", true) // 让 LoginActivity 知道是从哪里来的
                startActivityForResult(intent, 1001)

            }
            // 弹出日期选择器
            showDatePickerDialog()
        }

        // 获取从 Intent 传递的 UUID
        val attractionUuid = intent.getStringExtra("attraction_uuid")
        if (attractionUuid != null) {
            fetchAttractionDetails(attractionUuid)
        }

        val backButton: ImageButton = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            // 关闭 Activity
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // 处理登录成功后的逻辑，比如刷新界面
            recreate() // 重新加载当前页面
        }
    }


    private fun fetchAttractionDetails(uuid: String) {
        val apiKey = "6IBB6PFfArqu7dvgOJaXFZKyqAN9uJAC" // 替换为你的 API Key
        val contentLanguage = "en"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.fetchAttractionByUUID(uuid = uuid, apiKey = apiKey, contentLanguage = contentLanguage)
                if (response.isSuccessful) {
                    val attractionResponse = response.body()
                    val attractionData = attractionResponse?.data?.firstOrNull()


                    if (attractionData != null) {
                        runOnUiThread {
                            attractionNameTextView.text = attractionData.name
                            attractionDescriptionTextView.text = attractionData.description
                            attractionRatingTextView.text = attractionData.rating?.toString() ?: "No rating"
                            attractionAddressTextView.text = attractionData.address.formattedAddress()
                            attractionPriceTextView.text = attractionData.pricing?.others ?: "Price not available"

                            // 加载景点图片，最多加载 5 张
                            val imageUrls = attractionData.images?.take(5)?.map { image ->
                                "http://10.0.2.2:8080/proxy/media/${image.uuid}"
                            } ?: emptyList()

                            // 设置图片切换功能
                            startImageSlideshow(imageUrls)

                            // 清空已有的设施列表，防止重复添加
                            wordListContainer.removeAllViews()

                            // 添加 "Tags" 标题
                            val tagsTitle = TextView(this@AttractionDetailActivity).apply {
                                text = "Tags"
                                textSize = 18f
                                setTextColor(Color.parseColor("#FF232323"))
                                setTypeface(null, Typeface.BOLD)
                            }
                            wordListContainer.addView(tagsTitle)

                            // 解析标签并动态添加
                            attractionData.tags?.forEach { tag ->
                                val wordTextView = TextView(this@AttractionDetailActivity).apply {
                                    text = "· $tag"
                                    textSize = 16f
                                    setTextColor(Color.BLACK)
                                }
                                wordListContainer.addView(wordTextView)
                            }

                            // 添加 "Business Hours" 标题
                            val businessHoursTitle = TextView(this@AttractionDetailActivity).apply {
                                text = "Business Hours"
                                textSize = 18f
                                setTextColor(Color.parseColor("#FF232323"))
                                setTypeface(null, Typeface.BOLD)
                            }
                            wordListContainer.addView(businessHoursTitle)

                            // 获取Business Hour
                            businessHours = attractionData.businessHour ?: emptyList()

                            // 显示 Business Hour 相关内容
                            displayBusinessHours()

                            // 解析并动态显示 Business Hour 信息
                            attractionData.businessHour?.forEach { businessHour ->
                                val businessInfo = StringBuilder()

                                // 显示 day (可以是 daily, monday, public_holiday 等)
                                businessInfo.append("Day: ${businessHour.day.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.ROOT
                                    ) else it.toString()
                                }}\n")

                                // 显示开放时间和关闭时间
                                businessInfo.append("Hours: ${businessHour.openTime} - ${businessHour.closeTime}\n")

                                // 如果有描述（如特殊活动），则显示
                                if (!businessHour.description.isNullOrEmpty()) {
                                    businessInfo.append("Description: ${businessHour.description}\n")
                                }

                                // 创建 TextView 显示 Business Hour 信息
                                val businessTextView = TextView(this@AttractionDetailActivity).apply {
                                    text = businessInfo.toString()
                                    textSize = 16f
                                    setTextColor(Color.BLACK)
                                    setPadding(0, 10, 0, 10)
                                }
                                wordListContainer.addView(businessTextView)
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@AttractionDetailActivity, "Failed to load attraction details", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AttractionDetailActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private var currentImageIndex = 0
    private var imageUrls: List<String> = emptyList()

    // image switching
    private val handler = Handler(Looper.getMainLooper()) // ✅ 只用一个 handler 进行管理

    private fun startImageSlideshow(urls: List<String>) {
        imageUrls = urls
        if (imageUrls.isNotEmpty()) {
            // 确保 Activity 仍然存活
            if (!isDestroyed && !isFinishing) {
                // 初始加载第一张图片，保持当前图片
                Glide.with(this)
                    .load(imageUrls[currentImageIndex])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 启用缓存
                    .placeholder(R.drawable.attraction_placeholder_image)  // 初始加载时显示占位符
                    .error(R.drawable.attraction_placeholder_image)  // 加载失败显示占位符
                    .into(attractionImageView)
            }

            // 启动定时器切换图片
            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (!isDestroyed && !isFinishing && imageUrls.isNotEmpty()) {
                        // 计算下一张图片的索引
                        val nextImageIndex = (currentImageIndex + 1) % imageUrls.size

                        // 先加载下一张图片（保持当前图片不变，直到新图片加载完成）
                        Glide.with(this@AttractionDetailActivity)
                            .load(imageUrls[nextImageIndex])
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(attractionImageView)  // 只在图片加载完成后切换

                        // 更新 currentImageIndex 为下一张图片
                        currentImageIndex = nextImageIndex

                        // 设置延迟时间，继续切换
                        handler.postDelayed(this, 2000)
                    }
                }
            }, 2000) // 延迟启动
        }
    }

    /**
     * 弹出日期选择器 Dialog
     */
    // 显示 Business Hours 信息
    private fun displayBusinessHours() {
        wordListContainer.removeAllViews()

        val businessHoursTitle = TextView(this@AttractionDetailActivity).apply {
            text = "Business Hours"
            textSize = 18f
            setTextColor(Color.parseColor("#FF232323"))
            setTypeface(null, Typeface.BOLD)
        }
        wordListContainer.addView(businessHoursTitle)

        businessHours.forEach { businessHour ->
            val businessInfo = StringBuilder()
            businessInfo.append("Day: ${businessHour.day.replaceFirstChar { it.uppercase(Locale.ROOT) }}\n")
            businessInfo.append("Hours: ${businessHour.openTime} - ${businessHour.closeTime}\n")
            if (!businessHour.description.isNullOrEmpty()) {
                businessInfo.append("Description: ${businessHour.description}\n")
            }

            val businessTextView = TextView(this@AttractionDetailActivity).apply {
                text = businessInfo.toString()
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(0, 10, 0, 10)
            }
            wordListContainer.addView(businessTextView)
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = CalendarView(this)
        val availableDates = parseAvailableDates()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Date for Booking")
        builder.setView(datePicker)

        datePicker.minDate = System.currentTimeMillis() // 最小日期：今天
        datePicker.maxDate = System.currentTimeMillis() + (60L * 24 * 60 * 60 * 1000) // **最大日期：今天 + 60 天**

        var lastValidDate = System.currentTimeMillis()

        datePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedTime = parseDateToTimestamp(year, month, dayOfMonth)

            if (availableDates.contains(selectedTime)) {
                lastValidDate = selectedTime
                showTimePickerDialog("$dayOfMonth-${month + 1}-$year")
            } else {
                Toast.makeText(this, "This date is not available", Toast.LENGTH_SHORT).show()
                datePicker.date = lastValidDate // ❌ 立即回滚
            }
        }

        builder.setPositiveButton("Confirm") { dialog, _ -> dialog.dismiss() }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    /**
     * 解析 `businessHours` 获取可选日期（支持 daily & public_holiday）
     */
    private fun parseAvailableDates(): Set<Long> {
        val availableDates = mutableSetOf<Long>()
        val calendar = Calendar.getInstance()

        val today = Calendar.getInstance()
        val maxDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 60) } // 限制到未来 60 天

        businessHours.forEach { businessHour ->
            val dayOfWeek = getDayOfWeek(businessHour.day)
            if (dayOfWeek != -1) {
                for (i in 0..60) { // **只计算未来 60 天**
                    calendar.timeInMillis = today.timeInMillis
                    calendar.add(Calendar.DAY_OF_YEAR, i)

                    if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0) // 归零，确保匹配

                        if (calendar.timeInMillis <= maxDate.timeInMillis) {
                            availableDates.add(calendar.timeInMillis)
                        }
                    }
                }
            }
        }

        // ✅ Debug 打印
        availableDates.forEach {
            val debugDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            println("Available Date: $debugDate ($it)")
        }

        return availableDates
    }

    /**
     * 获取 `public_holiday` 具体日期（示例：需要从 API 获取）
     */
    private fun getPublicHolidays(): List<Long> {
        val holidayDates = mutableListOf<Long>()
        val calendar = Calendar.getInstance()

        // 示例假期（实际应该从 API 获取）
        val publicHolidayList = listOf("2025-01-01", "2025-05-01", "2025-12-25")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        publicHolidayList.forEach { dateString ->
            val date = dateFormat.parse(dateString)
            if (date != null) {
                calendar.time = date
                holidayDates.add(calendar.timeInMillis)
            }
        }

        return holidayDates
    }

    /**
     * 获取 `day` 对应的 `Calendar.DAY_OF_WEEK`
     */
    private fun getDayOfWeek(day: String): Int {
        return when (day.lowercase(Locale.ROOT)) {
            "monday" -> Calendar.MONDAY
            "tuesday" -> Calendar.TUESDAY
            "wednesday" -> Calendar.WEDNESDAY
            "thursday" -> Calendar.THURSDAY
            "friday" -> Calendar.FRIDAY
            "saturday" -> Calendar.SATURDAY
            "sunday" -> Calendar.SUNDAY
            "public_holiday" -> -1
            else -> -1
        }
    }

    /**
     * 显示时间选择器，仅允许 `openTime - closeTime`
     */
    private fun showTimePickerDialog(selectedDate: String) {
        val selectedDay = getDayOfWeekString(selectedDate)

        val businessHour = businessHours.find { it.day == selectedDay || it.day == "daily" }
        if (businessHour == null) {
            Toast.makeText(this, "No available time slots for this date.", Toast.LENGTH_SHORT).show()
            return
        }

        val (openHour, openMinute) = parseTimeString(businessHour.openTime)
        val (closeHour, closeMinute) = parseTimeString(businessHour.closeTime)

        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
            if (isTimeInRange(hourOfDay, minute, openHour, openMinute, closeHour, closeMinute)) {
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)

                // 显示确认预订弹窗
                AlertDialog.Builder(this)
                    .setTitle("Confirm Booking")
                    .setMessage("Book for $selectedDate at $selectedTime?")
                    .setPositiveButton("Confirm") { _, _ ->
                        bookAttraction(selectedDate, selectedTime, 1) // 默认票数 1
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            } else {
                Toast.makeText(this, "Selected time is outside business hours!", Toast.LENGTH_SHORT).show()
            }
        }, openHour, openMinute, true)

        timePickerDialog.show()
    }

    private fun parseTimeString(timeString: String): Pair<Int, Int> {
        val parts = timeString.split(":")
        return if (parts.size == 2) {
            Pair(parts[0].toInt(), parts[1].toInt())
        } else {
            Pair(0, 0) // 解析失败默认返回 00:00
        }
    }

    private fun isTimeInRange(selectedHour: Int, selectedMinute: Int,
                              openHour: Int, openMinute: Int,
                              closeHour: Int, closeMinute: Int): Boolean {
        val selectedTotalMinutes = selectedHour * 60 + selectedMinute
        val openTotalMinutes = openHour * 60 + openMinute
        val closeTotalMinutes = closeHour * 60 + closeMinute

        return selectedTotalMinutes in openTotalMinutes..closeTotalMinutes
    }

    /**
     * 解析日期字符串为 `dayOfWeek`
     */
    private fun getDayOfWeekString(dateString: String): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: return ""
        val calendar = Calendar.getInstance()
        calendar.time = date

        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "monday"
            Calendar.TUESDAY -> "tuesday"
            Calendar.WEDNESDAY -> "wednesday"
            Calendar.THURSDAY -> "thursday"
            Calendar.FRIDAY -> "friday"
            Calendar.SATURDAY -> "saturday"
            Calendar.SUNDAY -> "sunday"
            else -> ""
        }
    }

    /**
     * 将 `year, month, day` 转换为时间戳
     */
    private fun parseDateToTimestamp(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0) // 设为 00:00:00 确保匹配
        calendar.set(Calendar.MILLISECOND, 0)

        val timestamp = calendar.timeInMillis

        // ✅ 调试打印，确保 selectedTime 正确
        val debugDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(timestamp)
        println("Selected Date Timestamp: $debugDate ($timestamp)")

        return timestamp
    }

    private fun bookAttraction(selectedDate: String, selectedTime: String, numberOfTickets: Int) {
        val userId = getUserId() ?: run {
            Toast.makeText(this, "🚨 User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val attractionUuid = intent.getStringExtra("attraction_uuid") ?: run {
            Toast.makeText(this, "🚨 Missing attraction UUID!", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ 修正 visitTime 格式
        val formattedDate = formatDateToBackendFormat(selectedDate)
        val formattedTime = formatTimeToBackendFormat(selectedDate, selectedTime) // 👈 新增转换

        val request = AttractionBookingRequest(
            uuid = attractionUuid,
            userId = userId,
            visitDate = formattedDate,
            visitTime = formattedTime, // ✅ 传递修正格式
            numberOfTickets = numberOfTickets,
            price = "66.66"
        )

        println("📌 Booking Request: $request") // ✅ 打印请求参数

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userApiService.createAttractionBooking(request)

                if (response.isSuccessful) {
                    val bookingResponse = response.body()
                    println("✅ Booking Successful: $bookingResponse")

                    runOnUiThread {
                        Toast.makeText(this@AttractionDetailActivity, "Booking Successful!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("🚨 Booking Failed! Response Code: ${response.code()}")
                    println("🚨 Error Body: $errorBody")

                    runOnUiThread {
                        Toast.makeText(this@AttractionDetailActivity, "Booking Failed: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                println("🚨 Booking Exception: ${e.message}")

                runOnUiThread {
                    Toast.makeText(this@AttractionDetailActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getUserId(): String? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", null) // 读取 userId
    }

    private fun formatTimeToBackendFormat(date: String, time: String): String {
        // 确保 date 是 yyyy-MM-dd 格式
        val formattedDate = formatDateToBackendFormat(date) // 先转换日期
        return "${formattedDate}T${time}:00" // 返回标准格式 "2025-02-10T11:55:00"
    }

    private fun formatDateToBackendFormat(date: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) // 用户选择的格式
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // 后端格式
        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate!!)
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // ✅ 防止定时任务继续执行
        Glide.with(applicationContext).clear(attractionImageView) // ✅ 清理 Glide 加载任务
    }
}
