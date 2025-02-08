package iss.nus.edu.sg.mygo.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.key
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.AttractionAdapter
import iss.nus.edu.sg.mygo.adapter.SpaceItemDecoration
import iss.nus.edu.sg.mygo.api.models.AttractionData
import iss.nus.edu.sg.mygo.api.service.AttractionApiService
import iss.nus.edu.sg.mygo.models.Attraction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttractionMainActivity : AppCompatActivity() {

    private lateinit var attractionAdapter: AttractionAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var backButton: ImageButton

    private val apiService = AttractionApiService.create()
    private var offset = 0 // 分页偏移量
    private val limit = 7 // 每次加载的条数
    private var isLoading = false // 避免重复请求
    private var keyword: String = "" // 当前搜索关键字

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attraction_search)

        recyclerView = findViewById(R.id.recyclerView_hotels)
        searchEditText = findViewById(R.id.text_search_attractions)
        backButton = findViewById(R.id.button_back)

        recyclerView.layoutManager = GridLayoutManager(this,2)

        // 设置每个卡片之间的间距
        recyclerView.addItemDecoration(SpaceItemDecoration(dpToPx(8))) // 修改间距为 8dp

        attractionAdapter = AttractionAdapter(mutableListOf()) { position ->
            val attraction = attractionAdapter.getItem(position)
            attraction?.let { openAttractionDetail(it) }
        }
        recyclerView.adapter = attractionAdapter

        // ✅ 默认加载景点
        fetchAttractions()

        // ✅ 监听搜索框输入
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                keyword = s.toString().trim()
                offset = 0 // 重置分页
                attractionAdapter.updateData(emptyList()) // 清空当前数据
                fetchAttractions() // 执行搜索
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // ✅ 监听 RecyclerView 滑动，滚动到底部时加载更多
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItem >= totalItemCount - 1) {
                    fetchAttractions() // 加载更多
                }
            }
        })

        // ✅ 处理返回按钮
        backButton.setOnClickListener {
            finish()
        }
    }

    /**
     *  获取景点列表（支持分页 & 关键词搜索）
     */
    private fun fetchAttractions() {
        if (isLoading) return
        isLoading = true // 设置加载状态，避免重复请求

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if(keyword.isEmpty()){
                    apiService.fetchAttraction(
                        offset = offset,
                        limit = limit,
                        dataset = "attractions",
                        apiKey = "6IBB6PFfArqu7dvgOJaXFZKyqAN9uJAC",
                        contentLanguage = "en"
                    )
                }else {
                    apiService.fetchAttractionByKeyword(
                        offset = offset,
                        limit = limit,
                        keyword = keyword,
                        apiKey = "6IBB6PFfArqu7dvgOJaXFZKyqAN9uJAC"
                    )
                }

                if (response.isSuccessful) {
                    val attractionResponse = response.body()
                    attractionResponse?.let {
                        val attractionList = mapAttractionDataToAttractionList(it.data)
                        runOnUiThread {
                            val currentData = attractionAdapter.getItemList() // 获取当前数据列表
                            attractionAdapter.updateData(currentData + attractionList) // 拼接新的数据
                            offset += limit // 更新偏移量
                        }
                    }
                } else {
                    showError("Failed to load attractions")
                }
            } catch (e: Exception) {
                showError("Network Error: ${e.message}")
            } finally {
                isLoading = false // 重置加载状态
            }
        }
    }

    /**
     *  将 API `AttractionData` 转换为 `Attraction` 数据类型
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
     *  打开 `AttractionDetailActivity`
     */
    private fun openAttractionDetail(attraction: Attraction) {
        val intent = Intent(this, AttractionDetailActivity::class.java)
        intent.putExtra("attraction_uuid", attraction.uuid)
        startActivity(intent)
    }

    /**
     *  显示错误消息
     */
    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this@AttractionMainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * ✅ `dp` 转 `px`
     */
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
