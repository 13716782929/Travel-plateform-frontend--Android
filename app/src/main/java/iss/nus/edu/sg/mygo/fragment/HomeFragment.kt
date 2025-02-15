package iss.nus.edu.sg.mygo.fragment

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.AttractionAdapter
import iss.nus.edu.sg.mygo.adapter.HotelAdapter
import iss.nus.edu.sg.mygo.adapter.HotelAdapter2
import iss.nus.edu.sg.mygo.adapter.SpaceItemDecoration
import iss.nus.edu.sg.mygo.api.models.Accommodation
import iss.nus.edu.sg.mygo.api.service.AttractionApiService
import iss.nus.edu.sg.mygo.api.models.AttractionData
import iss.nus.edu.sg.mygo.api.service.AccommodationApiService
import iss.nus.edu.sg.mygo.api.service.HotelApiService
import iss.nus.edu.sg.mygo.api.service.MediaApiService
import iss.nus.edu.sg.mygo.api.service.RecommendationApiService
import iss.nus.edu.sg.mygo.databinding.ActivityAttractionDetailBinding
import iss.nus.edu.sg.mygo.databinding.HomeFragmentBinding
import iss.nus.edu.sg.mygo.home.AttractionDetailActivity
import iss.nus.edu.sg.mygo.home.AttractionMainActivity
import iss.nus.edu.sg.mygo.home.FlightMainActivity
import iss.nus.edu.sg.mygo.home.FlightSearchActivity
import iss.nus.edu.sg.mygo.home.HotelDetailActivity
import iss.nus.edu.sg.mygo.home.HotelMainActivity
import iss.nus.edu.sg.mygo.models.Attraction
import iss.nus.edu.sg.mygo.models.Hotel
import kotlinx.coroutines.launch
import androidx.fragment.app.viewModels
import iss.nus.edu.sg.mygo.api.service.FlightApiService
import iss.nus.edu.sg.mygo.home.FlightDetailActivity
import iss.nus.edu.sg.mygo.models.Flight
import iss.nus.edu.sg.mygo.models.FlightBooking
import iss.nus.edu.sg.mygo.models.FlightTicketViewModel
import iss.nus.edu.sg.mygo.sessions.SessionManager
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @ClassName HomeFragment
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/1/28
 * @Version 1.3
 */
class HomeFragment : Fragment(R.layout.home_fragment) {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AttractionAdapter
    private lateinit var apiService: AttractionApiService  // 使用 AttractionApiService
    private lateinit var mediaApiService: MediaApiService  // 使用 MediaApiService 获取图片

    private lateinit var sessionManager: SessionManager // 添加 SessionManager 变量
    private lateinit var hotelAdapter: HotelAdapter2
    private lateinit var apiService2: HotelApiService // 使用 HotelApiService 获取酒店数据
    private lateinit var recommendationApiService: RecommendationApiService // 添加推荐 API Service

    // ✅ 创建 FlightTicketViewModel 实例
    private val flightViewModel: FlightTicketViewModel by viewModels {
        FlightTicketViewModel.FlightViewModelFactory(FlightApiService.create())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 初始化 API 服务
        apiService = AttractionApiService.create()
        apiService2 = HotelApiService.create()
        mediaApiService = MediaApiService.create()
        recommendationApiService = RecommendationApiService.create() // ✅ 初始化推荐 API
        _binding = HomeFragmentBinding.bind(view) // ✅ 启用 ViewBinding

        sessionManager = SessionManager(requireContext()) // ✅ 初始化 SessionManager
        checkUserLoginStatus() // ✅ 检查用户是否已登录

        setupRecyclerView()  // 初始化 RecyclerView
        setupClickListeners() // ✅ 统一管理点击事件
//        fetchAttractions()    // 调用 API 获取数据
        fetchHotels()
        fetchRecommendedAttractions() // ✅ 获取推荐的景点
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkUserLoginStatus() {
        if (!sessionManager.isLoggedIn()) {
            // ❌ 用户未登录，隐藏机票视图
            binding.layoutControllerTickets.visibility = View.GONE
        } else {
            fetchUserFlights()  // ✅ 获取用户的航班预订数据
            observeFlightData() // ✅ 监听 `FlightBooking` 数据
        }
    }

    /**
     * ✅ 设置 RecyclerView 及点击事件
     */
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.addItemDecoration(SpaceItemDecoration(dpToPx(10)))

        binding.recyclerViewHotels.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewHotels.addItemDecoration(SpaceItemDecoration(dpToPx(10)))

        // ✅ 初始化 Adapter
        adapter = AttractionAdapter(mutableListOf()) { position ->
            adapter.getItem(position)?.let { selectedAttraction ->
                val intent = Intent(requireContext(), AttractionDetailActivity::class.java).apply {
                    putExtra("attraction_uuid", selectedAttraction.uuid)  // 传递 UUID
                }
                startActivity(intent)
            }
        }

        hotelAdapter = HotelAdapter2(mutableListOf()) { position ->
            hotelAdapter.getItem(position)?.let { selectedHotels ->
                val intent = Intent(requireContext(), HotelDetailActivity::class.java).apply {
                    putExtra("hotel_uuid", selectedHotels.uuid)  // 传递 UUID
                }
                startActivity(intent)
            }
        }


        binding.recyclerView.adapter = adapter
        binding.recyclerViewHotels.adapter = hotelAdapter
    }

    /**
     *  处理 Home 页面按钮点击事件
     */
    private fun setupClickListeners() {
        binding.hotels.setOnClickListener {
            startActivity(Intent(requireContext(), HotelMainActivity::class.java))
        }
        binding.attractions.setOnClickListener{
            startActivity(Intent(requireContext(), AttractionMainActivity::class.java))
        }
        binding.flight.setOnClickListener{
            startActivity(Intent(requireContext(), FlightSearchActivity::class.java))
        }
    }

    /**
     * ✅ 获取 `Attraction` 数据并映射到 `RecyclerView`
     */
    private fun fetchAttractions() {
        val apiKey = "6IBB6PFfArqu7dvgOJaXFZKyqAN9uJAC"
        val contentLanguage = "en"

        lifecycleScope.launch {
            try {
                val response = apiService.fetchAttraction(0, 7, "attractions", apiKey, contentLanguage)
                if (response.isSuccessful) {
                    val attractionResponse = response.body()
                    attractionResponse?.let {
                        val attractionList = mapAttractionDataToAttractionList(it.data)
                        adapter.updateData(attractionList)  // ✅ 更新 Adapter 数据
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load attractions", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * ✅ 获取 `Hotel` 数据并映射到 `RecyclerView`
     */
    private fun fetchHotels() {
        val apiKey = "6IBB6PFfArqu7dvgOJaXFZKyqAN9uJAC"
        val contentLanguage = "en"

        lifecycleScope.launch {
            try {
                val response = apiService2.fetchHotel(0, 17, "accommodation", apiKey, contentLanguage)
                if (response.isSuccessful) {
                    val hotelResponse = response.body()
                    hotelResponse?.let {
                        val hotelList = mapAccommoationDataToHotelList(it.data)
                            .filter { hotel ->
                                if (hotel.imageUrl.isNullOrEmpty()) {
                                    Log.d("FILTER", "过滤无图片的酒店: ${hotel.name}")
                                    return@filter false
                                }
                                if (hotel.price.isNullOrEmpty() || hotel.price == "价格不可用") {
                                    Log.d("FILTER", "过滤无价格的酒店: ${hotel.name}")
                                    return@filter false
                                }
                                true
                            }

                        hotelAdapter.updateData(hotelList)  // ✅ 更新 Adapter 数据
                    }
                } else {
                    Log.e("HomeFragment", "Failed response: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to load hotels", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * ✅ 将 API `AttractionData` 转换为 `Attraction`
     */
    private fun mapAttractionDataToAttractionList(attractionDataList: List<AttractionData>): List<Attraction> {
        return attractionDataList.map { attractionData ->
            Attraction(
                uuid = attractionData.uuid,
                name = attractionData.name ?: "Unknown Attraction",
                address = attractionData.address.formattedAddress(),
                description = attractionData.description ?: "No description available",
                rate = attractionData.rating ?: 0.0,
                price = attractionData.pricing.formattedPrice() ?: "$: N/A",
                imageUuid = attractionData.thumbnails.firstOrNull()?.uuid ?: ""
            )
        }
    }

    /**
     * ✅ 将 API `AttractionData` 转换为 `Attraction`
     */
    private fun mapAccommoationDataToHotelList(accommodationDataList: List<Accommodation>): List<Hotel> {
        return accommodationDataList.map { accommodationData ->
            Hotel(
                uuid = accommodationData.uuid,
                name = accommodationData.name ?: "Unknown Accommodation",
                address = accommodationData.address.formattedAddress(),
                description = accommodationData.description ?: "No description available",
                rating = (accommodationData.rating ?: 0.0).toString(),
                price = accommodationData.leadInRoomRates ?: "$: N/A",
                imageUrl = accommodationData.thumbnails?.firstOrNull()?.uuid ?: ""
            )
        }
    }

    /**
     * ✅ 获取推荐的景点
     */
    private fun fetchRecommendedAttractions() {
        val userId = getUserIdFromLocalStorage()?.toIntOrNull() // ✅ 从本地存储获取 userId
        Log.e("HomeFragment", "Fetched user ID: $userId")  // 🛠️ 日志跟踪用户 ID

        if (userId == null) {
            Log.e("HomeFragment", "❌ User ID not found in local storage")  // 🛠️ 日志跟踪失败情况
            return
        }

        val apiKey = "6IBB6PFfArqu7dvgOJaXFZKyqAN9uJAC" // 替换为你的 API Key
        val contentLanguage = "en"

        lifecycleScope.launch {
            try {
                Log.e("HomeFragment", "📡 Requesting recommended attractions for user ID: $userId")
                val recommendationResponse = recommendationApiService.fetchPersonalizedAttractions(userId)

                if (recommendationResponse.isSuccessful) {
                    val recommendationList = recommendationResponse.body() ?: emptyList()
                    Log.e("HomeFragment", "✅ Successfully fetched recommendations. Count: ${recommendationList.size}")

                    // ✅ 过滤重复 UUID
                    val uniqueUuids = recommendationList.mapNotNull { it.uuid }.distinct()
                    Log.e("HomeFragment", "🔍 Extracted unique UUIDs from recommendations: $uniqueUuids")

                    if (uniqueUuids.isEmpty()) {
                        Log.e("HomeFragment", "❌ No recommended attractions found (UUIDs list is empty)")
                        return@launch
                    }

                    // 3. 获取每个景点的详细信息
                    val attractionDataList = mutableListOf<AttractionData>()
                    for (uuid in uniqueUuids) {
                        Log.e("HomeFragment", "📡 Fetching details for attraction UUID: $uuid")
                        val attractionResponse = apiService.fetchAttractionByUUID(uuid = uuid, apiKey = apiKey, contentLanguage = contentLanguage)

                        if (attractionResponse.isSuccessful) {
                            attractionResponse.body()?.data?.let {
                                attractionDataList.addAll(it)
                                Log.e("HomeFragment", "✅ Successfully fetched attraction details for UUID: $uuid")
                            }
                        } else {
                            Log.e("HomeFragment", "❌ Failed to fetch attraction details for UUID: $uuid, Response Code: ${attractionResponse.code()}")
                        }
                    }

                    // 4. 转换数据
                    Log.e("HomeFragment", "🔄 Mapping attraction data to list...")
                    val attractionList = mapAttractionDataToAttractionList(attractionDataList)
                    Log.e("HomeFragment", "✅ Mapped attraction data. Count: ${attractionList.size}")

                    // 5. 更新 RecyclerView 适配器
                    adapter.updateData(attractionList)
                    Log.e("HomeFragment", "✅ Adapter updated with new attraction data.")

                } else {
                    Log.e("HomeFragment", "❌ Failed to fetch recommendations. Error: ${recommendationResponse.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("HomeFragment", "❌ Error fetching recommendations: ${e.message}", e)
            }
        }
    }

    /**
     * ✅ 获取本地存储的 userId
     */
    private fun getUserIdFromLocalStorage(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        Log.e("HomeFragment", "📂 Retrieved user ID from storage: $userId")  // 🛠️ 日志跟踪获取本地存储的 user_id
        return userId
    }

    /**
     * ✅ 1. 获取用户的航班预订信息
     */
    private fun fetchUserFlights() {
        val userId = getUserIdFromLocalStorage()?.toIntOrNull()
        if (userId != null) {
            flightViewModel.getUserFlightBookings(userId)
        } else {
            Log.e("HomeFragment-Flight", "❌ User ID not found in local storage")
        }
    }

    /**
     * ✅ 2. 监听 Flight 数据并更新 UI
     */
    private fun observeFlightData() {
        lifecycleScope.launch {
            flightViewModel.flightBookings.collectLatest { bookings ->
                if (bookings.isNotEmpty()) {
                    val booking = bookings.first() // 只展示第一个航班预订
                    updateFlightBookingUI(booking)

                    booking.flightId?.let { flightId ->

                        // ✅ 点击机票，跳转到 FlightDetailActivity
                        binding.layoutControllerTickets.setOnClickListener {
                            val intent = Intent(requireContext(), FlightDetailActivity::class.java).apply {
                                putExtra("flightId", flightId) // ✅ 传递 flightId
                            }
                            startActivity(intent)
                        }
                    }
                } else {
                    binding.layoutControllerTickets.visibility = View.GONE // 隐藏 UI
                }
            }
        }

        lifecycleScope.launch {
            flightViewModel.flights.collectLatest { flights ->
                if (flights.isNotEmpty()) {
                    val flight = flights.first() // 获取第一个航班详情
                    updateFlightDetailsUI(flight)
                }
            }
        }
    }

    /**
     * ✅ 3. 更新 `FlightBooking` 相关 UI
     */
    private fun updateFlightBookingUI(flightBooking: FlightBooking) {
        binding.bookingID.text = flightBooking.bookingId?.toString() ?: "N/A"
        binding.layoutControllerTickets.visibility = View.VISIBLE
    }

    /**
     * ✅ 4. 更新 `Flight` 详情 UI
     */
    private fun updateFlightDetailsUI(flight: Flight) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm") // 只提取时间
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 只提取日期

        val departureDateTime = LocalDateTime.parse(flight.departureTime, formatter)
        val arrivalDateTime = LocalDateTime.parse(flight.arrivalTime, formatter)

        val departureTime = departureDateTime.format(timeFormatter) // "22:17"
        val arrivalTime = arrivalDateTime.format(timeFormatter) // "11:17"

        val departureDate = departureDateTime.format(dateFormatter) // "2025-03-07"
        val arrivalDate = arrivalDateTime.format(dateFormatter) // "2025-03-08"

        binding.upcomingTime.text = departureDate
        binding.departureLocation.text = flight.departureCity
        binding.departureTime.text = departureTime
        binding.arrivalLocation.text = flight.arrivalCity
        binding.arrivalTime.text = arrivalTime
        binding.adFlight.text = flight.airline
    }

    /**
     * ✅ `dp` 转 `px`
     */
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
