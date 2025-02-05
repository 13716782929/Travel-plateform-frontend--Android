package iss.nus.edu.sg.mygo.home

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.HotelAdapter
import iss.nus.edu.sg.mygo.adapter.HotelSpaceItemDecoration
import iss.nus.edu.sg.mygo.api.AccommodationApiService
import iss.nus.edu.sg.mygo.models.AccommodationImageResponse
import iss.nus.edu.sg.mygo.models.AccommodationResponse
import iss.nus.edu.sg.mygo.models.Hotel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HotelSearchActivity : AppCompatActivity() {

    private lateinit var hotelAdapter: HotelAdapter
    private lateinit var recyclerViewHotels: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_search)

        // 获取 UI 组件
        val checkInDateTextView = findViewById<TextView>(R.id.text_check_in_date)
        val nightsTextView = findViewById<TextView>(R.id.text_nights)
        val backButton = findViewById<ImageButton>(R.id.button_back)
        searchEditText = findViewById(R.id.text_search_hotels)

        // 获取 Intent 传递的数据
        val checkInDate = intent.getStringExtra("checkInDate") ?: "Not Provided"
        val nights = intent.getStringExtra("nights") ?: "Not Provided"

        // 显示传递的数据
        checkInDateTextView.text = checkInDate
        nightsTextView.text = nights

        // 监听返回按钮点击事件
        backButton.setOnClickListener {
            finish()
        }

        // 初始化 RecyclerView
        recyclerViewHotels = findViewById(R.id.recyclerView_hotels)
        recyclerViewHotels.layoutManager = LinearLayoutManager(this)
        recyclerViewHotels.addItemDecoration(HotelSpaceItemDecoration(16))
        hotelAdapter = HotelAdapter(emptyList()) // 初始为空列表
        recyclerViewHotels.adapter = hotelAdapter

        searchEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {

                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchHotels(query)
                } else {
                    Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show()
                }

                // 隐藏键盘
                val inputMethodManager = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)

                true // 表示事件已处理
            } else {
                false
            }
        }


    }

    // 调用 API 搜索酒店
    private fun searchHotels(keyword: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.stb.gov.sg/")  // 请替换为你的 API 基地址
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(AccommodationApiService::class.java)
        val apiKey = "pRUJyzyyzpRd557ynCs7JRZtoKYr6PPC" // 你的 API Key

        val call = apiService.searchAccommodation(apiKey, "en", "keyword", keyword)

        call.enqueue(object : Callback<AccommodationResponse> {
            override fun onResponse(call: Call<AccommodationResponse>, response: Response<AccommodationResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val accommodations = response.body()?.data ?: emptyList()
                    val hotels = mutableListOf<Hotel>()

                    // 遍历所有酒店，为每个酒店获取图片
                    accommodations.forEach { accommodation ->
                        val firstImageUuid = accommodation.thumbnails?.firstOrNull()?.libraryUuid
                        Log.d("UUID_CHECK", "First Image UUID: $firstImageUuid")

                        getImageUrl(firstImageUuid, apiService) { imageUrl ->
                            val hotel = Hotel(
                                uuid = accommodation.uuid,
                                name = accommodation.name,
                                address = listOfNotNull(
                                    accommodation.address.block,
                                    accommodation.address.streetName,
                                    accommodation.address.floorNumber?.let { floor ->
                                        accommodation.address.unitNumber?.let { unit -> "$floor-$unit" } ?: floor
                                    },
                                    accommodation.address.buildingName,
                                    accommodation.address.postalCode
                                ).joinToString(" ").ifEmpty { "Unknown Location" },
                                rating = accommodation.rating.toString(),
                                price = accommodation.leadInRoomRates ?: "Price Unavailable",
                                imageUrl = imageUrl
                            )

                            hotels.add(hotel)

                            // 当所有酒店的图片获取完毕后，更新 UI
                            if (hotels.size == accommodations.size) {
                                runOnUiThread { updateUI(hotels) }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@HotelSearchActivity, "搜索失败，请重试", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AccommodationResponse>, t: Throwable) {
                Toast.makeText(this@HotelSearchActivity, "网络错误：" + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }



    // 新增方法：通过 UUID 获取图片 URL
    private fun getImageUrl(uuid: String?, apiService: AccommodationApiService, callback: (String?) -> Unit) {
        if (uuid == null) {
            callback(null) // UUID 为空，直接返回
            return
        }

        val apiKey = "pRUJyzyyzpRd557ynCs7JRZtoKYr6PPC" // 你的 API Key

        val imageCall = apiService.getMediaDetails(apiKey, uuid)
        imageCall.enqueue(object : Callback<AccommodationImageResponse> {
            override fun onResponse(call: Call<AccommodationImageResponse>, response: Response<AccommodationImageResponse>) {
                Log.d("API_RESPONSE", "Response: ${response.body()}")
                val imageUrl = if (response.isSuccessful) {
                    response.body()?.data?.firstOrNull()?.url
                } else {
                    null
                }
                callback(imageUrl)
            }

            override fun onFailure(call: Call<AccommodationImageResponse>, t: Throwable) {
                callback(null)
            }
        })
    }




    // 更新 UI 显示搜索结果
    private fun updateUI(hotels: List<Hotel>) {
        hotelAdapter = HotelAdapter(hotels)
        recyclerViewHotels.adapter = hotelAdapter

//        // 更新酒店图片
//        hotels.forEach { hotel ->
//            hotel.imageUrl?.let { url ->
//                // 使用 Glide 加载图片到 ImageView
//                Glide.with(this)
//                    .load(url)
//                    .into(findViewById<ImageView>(R.id.hotel_image))  // 假设你有一个 ImageView 显示图片
//            }
//        }
    }



}
