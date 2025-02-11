package iss.nus.edu.sg.mygo.home

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.adapter.HotelAdapter
import iss.nus.edu.sg.mygo.adapter.HotelSpaceItemDecoration
import iss.nus.edu.sg.mygo.api.models.AccommodationResponse
import iss.nus.edu.sg.mygo.models.Hotel
import iss.nus.edu.sg.mygo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HotelSearchActivity : AppCompatActivity() {

    private lateinit var hotelAdapter: HotelAdapter
    private lateinit var recyclerViewHotels: RecyclerView
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_search)

        val backButton = findViewById<ImageButton>(R.id.button_back)
        searchEditText = findViewById(R.id.text_search_hotels)

        val destination = intent.getStringExtra("destination")
        if (!destination.isNullOrEmpty()) {
            searchEditText.setText(destination)
            searchHotels(destination)
        }

        backButton.setOnClickListener { finish() }

        recyclerViewHotels = findViewById(R.id.recyclerView_hotels)
        recyclerViewHotels.layoutManager = LinearLayoutManager(this)
        recyclerViewHotels.addItemDecoration(HotelSpaceItemDecoration(20))
        hotelAdapter = HotelAdapter(emptyList())
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

                val inputMethodManager = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)

                true
            } else {
                false
            }
        }
    }

    private fun searchHotels(keyword: String) {
        val apiService = RetrofitClient.accommodationApiService
        val call = apiService.searchAccommodation("XRi8QqZ4U85UqVJAniWoq67NG5CVFpzq", "en", "keyword", keyword)

        call.enqueue(object : Callback<AccommodationResponse> {
            override fun onResponse(call: Call<AccommodationResponse>, response: Response<AccommodationResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val accommodations = response.body()?.data ?: emptyList()
                    val hotels = mutableListOf<Hotel>()

                    accommodations.forEach { accommodation ->
                        val firstImageUuid = accommodation.thumbnails?.firstOrNull()?.uuid
                        if (firstImageUuid.isNullOrEmpty()) {
                            Log.d("FILTER", "过滤无图片的酒店: ${accommodation.name}")
                            return@forEach
                        }

                        if (accommodation.leadInRoomRates == null) {
                            Log.d("FILTER", "过滤无价格的酒店: ${accommodation.name}")
                            return@forEach
                        }

                        val addressParts = listOfNotNull(
                            accommodation.address.block,
                            accommodation.address.streetName,
                            accommodation.address.floorNumber?.let { floor ->
                                accommodation.address.unitNumber?.let { unit -> "$floor-$unit" } ?: floor
                            },
                            accommodation.address.buildingName,
                            accommodation.address.postalCode
                        )


                        getImageUrl(firstImageUuid) { imageUrl ->
                            val hotel = Hotel(
                                uuid = accommodation.uuid,
                                name = accommodation.name,
                                address = addressParts.joinToString(" "),
                                rating = accommodation.rating.toString(),
                                price = accommodation.leadInRoomRates ?: "价格不可用",
                                imageUrl = imageUrl
                            )

                            hotels.add(hotel)

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

    //        imageUrl = "http://10.0.2.2:8080/proxy/media/{uuid}?fileType=Small%20Thumbnail"

    private fun getImageUrl(uuid: String?, callback: (String?) -> Unit) {
        if (uuid == null) {
            callback(null)
            return
        }

        val imageUrl = "http://10.0.2.2:8080/proxy/media/$uuid?fileType=Small%20Thumbnail"
        // 直接生成本地代理服务器的 URL，替换 {uuid} 为实际的 uuid

        callback(imageUrl)
        // 立即执行回调函数，并返回生成的 imageUrl
    }

    private fun updateUI(hotels: List<Hotel>) {
        hotelAdapter = HotelAdapter(hotels)
        recyclerViewHotels.adapter = hotelAdapter
    }
}
