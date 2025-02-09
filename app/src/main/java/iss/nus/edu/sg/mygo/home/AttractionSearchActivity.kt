package iss.nus.edu.sg.mygo.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.AttractionAdapter
import iss.nus.edu.sg.mygo.adapter.AttractionSpaceItemDecoration
import iss.nus.edu.sg.mygo.adapter.SpaceItemDecoration
import iss.nus.edu.sg.mygo.api.models.AccommodationResponse
import iss.nus.edu.sg.mygo.api.models.AttractionData
import iss.nus.edu.sg.mygo.api.service.AttractionApiService
import iss.nus.edu.sg.mygo.api.service.MediaApiService
import iss.nus.edu.sg.mygo.models.Attraction
import iss.nus.edu.sg.mygo.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AttractionSearchActivity : AppCompatActivity() {

    private lateinit var attractionAdapter: AttractionAdapter
    private lateinit var recyclerViewAttractions: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var apiService: AttractionApiService
    private lateinit var mediaApiService: MediaApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attraction_search)

        // 初始化 API
        apiService = AttractionApiService.create()
        mediaApiService = MediaApiService.create()

        val backButton = findViewById<ImageButton>(R.id.button_back)
        searchEditText = findViewById(R.id.text_search_attractions)
        recyclerViewAttractions = findViewById(R.id.recyclerView_attractions)

        // 设置 RecyclerView 的布局管理器 (2列)
        recyclerViewAttractions.layoutManager = GridLayoutManager(this, 2)
        recyclerViewAttractions.addItemDecoration(AttractionSpaceItemDecoration(20))

        // 初始化 Adapter
        attractionAdapter = AttractionAdapter(mutableListOf()) { position ->
            attractionAdapter.getItem(position)?.let { selectedAttraction ->
                val intent = Intent(this, AttractionDetailActivity::class.java).apply {
                    putExtra("attraction_uuid", selectedAttraction.uuid)
                }
                startActivity(intent)
            }
        }
        recyclerViewAttractions.adapter = attractionAdapter

        // 处理返回按钮
        backButton.setOnClickListener { finish() }


        // 监听搜索输入框
        searchEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    fetchAttractions(query)
                } else {
                    Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show()
                }

                val inputMethodManager = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)

                true
            } else {
                false
            }
        }
    }

    /**
     * 从 API 获取景点数据
     */
    private fun fetchAttractions(keyword: String) {
        val apiKey = "6IBB6PFfArqu7dvgOJaXFZKyqAN9uJAC"
        val contentLanguage = "en"

        lifecycleScope.launch {
            try {
                val response = apiService.searchAttraction("keyword", keyword, apiKey, contentLanguage)
                if (response.isSuccessful) {
                    val attractionResponse = response.body()
                    attractionResponse?.let {
                        val attractionList = mapAttractionDataToAttractionList(it.data)
                        attractionAdapter.updateData(attractionList)
                    }
                } else {
                    Log.e("AttractionSearch", "API 请求失败: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(this@AttractionSearchActivity, "景点加载失败 (${response.code()})", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@AttractionSearchActivity, "错误: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 将 API `AttractionData` 转换为 `Attraction`
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

}
