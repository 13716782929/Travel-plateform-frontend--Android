package iss.nus.edu.sg.mygo.fragment

import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.AttractionAdapter
import iss.nus.edu.sg.mygo.adapter.SpaceItemDecoration
import iss.nus.edu.sg.mygo.api.service.AttractionApiService
import iss.nus.edu.sg.mygo.api.models.AttractionData
import iss.nus.edu.sg.mygo.api.service.MediaApiService
import iss.nus.edu.sg.mygo.databinding.ActivityAttractionDetailBinding
import iss.nus.edu.sg.mygo.databinding.HomeFragmentBinding
import iss.nus.edu.sg.mygo.home.AttractionDetailActivity
import iss.nus.edu.sg.mygo.home.HotelDetailActivity
import iss.nus.edu.sg.mygo.home.HotelMainActivity
import iss.nus.edu.sg.mygo.models.Attraction
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.home_fragment) {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AttractionAdapter
    private lateinit var apiService: AttractionApiService  // 使用 AttractionApiService
    private lateinit var mediaApiService: MediaApiService  // 使用 MediaApiService 获取图片

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 初始化 API 服务
        apiService = AttractionApiService.create()
        mediaApiService = MediaApiService.create()
        _binding = HomeFragmentBinding.bind(view) // ✅ 启用 ViewBinding

        setupRecyclerView()  // 初始化 RecyclerView
        setupClickListeners() // ✅ 统一管理点击事件
        fetchAttractions()    // 调用 API 获取数据
    }

    /**
     * ✅ 设置 RecyclerView 及点击事件
     */
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.addItemDecoration(SpaceItemDecoration(dpToPx(20)))

        // ✅ 初始化 Adapter
        adapter = AttractionAdapter(mutableListOf()) { position ->
            adapter.getItem(position)?.let { selectedAttraction ->
                val intent = Intent(requireContext(), AttractionDetailActivity::class.java).apply {
                    putExtra("attraction_uuid", selectedAttraction.uuid)  // 传递 UUID
                }
                startActivity(intent)
            }
        }
        binding.recyclerView.adapter = adapter
    }

    /**
     * ✅ 处理 Home 页面按钮点击事件
     */
    private fun setupClickListeners() {
        binding.hotels.setOnClickListener {
            startActivity(Intent(requireContext(), HotelMainActivity::class.java))
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
                imageUuid = attractionData.thumbnails.firstOrNull()?.uuid ?: ""
            )
        }
    }

    /**
     * ✅ `dp` 转 `px`
     */
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
