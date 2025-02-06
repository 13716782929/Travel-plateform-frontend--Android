package iss.nus.edu.sg.mygo.fragment
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.adapter.AttractionAdapter
import iss.nus.edu.sg.mygo.adapter.SpaceItemDecoration
import iss.nus.edu.sg.mygo.models.FlightInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.api.service.AttractionApiService
import iss.nus.edu.sg.mygo.api.models.AttractionData
import iss.nus.edu.sg.mygo.api.models.BusinessHour
import iss.nus.edu.sg.mygo.api.service.MediaApiService
import iss.nus.edu.sg.mygo.models.Attraction
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.home_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AttractionAdapter
    private lateinit var apiService: AttractionApiService  // 使用 AttractionApiService
    private lateinit var mediaApiService : MediaApiService // 使用 MediaApiService 获取图片

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 确保 Service 在这里正确初始化
        apiService = AttractionApiService.create()
        mediaApiService = MediaApiService.create()

        setupRecyclerView(view)  // 初始化 RecyclerView
        setupFlightInfoLayout(view)  // 设置航班信息的布局

        // 调用 API 获取数据
        fetchAttractions()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val spaceItemDecoration = SpaceItemDecoration(dpToPx(20))  // 20dp的间隔
        recyclerView.addItemDecoration(spaceItemDecoration)

        // 初始化 Adapter，避免 RecyclerView 报错
        adapter = AttractionAdapter(emptyList()) { position ->
            Toast.makeText(requireContext(), "选中了：${position}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
    }


    private fun setupFlightInfoLayout(view: View) {
        val flightInfoLayout: RelativeLayout = view.findViewById(R.id.layout_controller_tickets)
        val departureTextView: TextView = view.findViewById(R.id.departure_location)
        val arrivalTextView: TextView = view.findViewById(R.id.arrival_location)
        val departureTimeTextView: TextView = view.findViewById(R.id.departure_time)
        val arrivalTimeTextView: TextView = view.findViewById(R.id.arrival_time)

        val mockData = FlightInfo(
            departureLocation = "Singapore",
            arrivalLocation = "Tokyo",
            departureTime = "10:00 AM",
            arrivalTime = "3:00 PM"
        )

        if (mockData.departureLocation.isNullOrEmpty() ||
            mockData.arrivalLocation.isNullOrEmpty() ||
            mockData.departureTime.isNullOrEmpty() ||
            mockData.arrivalTime.isNullOrEmpty()) {
            flightInfoLayout.visibility = View.GONE
        } else {
            flightInfoLayout.visibility = View.VISIBLE
            departureTextView.text = mockData.departureLocation
            arrivalTextView.text = mockData.arrivalLocation
            departureTimeTextView.text = mockData.departureTime
            arrivalTimeTextView.text = mockData.arrivalTime
        }
    }

    // 获取 AttractionData 数据并映射为 Attraction 类型
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

                        // 🔹 遍历 Attraction 列表，获取每个景点的 imageUrl
                        attractionList.forEach { attraction ->
                            attraction.imageUrls = fetchImageUrls(attraction.uuid) // 获取图片 URL
                        }

                        // 🔹 传递 `imageUrls` 给 Adapter
                        adapter = AttractionAdapter(attractionList) { position ->
                            Toast.makeText(requireContext(), "选中了：${attractionList[position].name}", Toast.LENGTH_SHORT).show()
                        }
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load attractions", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun fetchImageUrls(uuid: String): List<String> {
        return try {
            val imageUrl = mediaApiService.getImageUrl(uuid) // 获取图片 URL
            listOf(imageUrl) // 返回单个 URL 列表
        } catch (e: Exception) {
            Log.e("ImageFetchError", "Error fetching image URL: ${e.message}")
            emptyList() // 返回空列表，避免崩溃
        }
    }



    // 将 AttractionData 转换为 Attraction 类
    private fun mapAttractionDataToAttractionList(attractionDataList: List<AttractionData>): List<Attraction> {
        return attractionDataList.map { attractionData ->
            mapAttractionDataToAttraction(attractionData)
        }
    }

    // 将单个 AttractionData 转换为 Attraction
    private fun mapAttractionDataToAttraction(attractionData: AttractionData): Attraction {
        return Attraction(
            uuid = attractionData.uuid,
            name = attractionData.name ?: "Unknown Attraction",
            address = attractionData.address.formattedAddress(), // 格式化地址
            latitude = attractionData.location.latitude ?: 0.0, // 假设 Location 可能为 null
            longitude = attractionData.location.longitude ?: 0.0, // 假设 Location 可能为 null
            description = attractionData.description ?: "No description available",
            price = attractionData.pricing.formattedPrice() , // 格式化价格
            openTime = formatBusinessHours(attractionData.businessHour), // 格式化开放时间
            ticketAvailability = attractionData.ticketed == "yes", // 根据 ticketed 字段判断
            imageUrls = attractionData.thumbnails.mapNotNull { it.url } // 过滤掉可能的 null 值
        )
    }


    // 格式化 business hour
    private fun formatBusinessHours(businessHours: List<BusinessHour>): String {
        return businessHours.joinToString { "${it.day}: ${it.openTime} - ${it.closeTime}" }
    }


    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}


