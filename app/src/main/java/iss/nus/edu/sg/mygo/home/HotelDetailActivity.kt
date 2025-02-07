package iss.nus.edu.sg.mygo.home

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.models.AccommodationImageResponse
import iss.nus.edu.sg.mygo.models.AccommodationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HotelDetailActivity : AppCompatActivity() {

    private lateinit var hotelNameTextView: TextView
    private lateinit var hotelDescriptionTextView: TextView
    private lateinit var hotelImageView: ImageView
    private lateinit var hotelAddressTextView: TextView
    private lateinit var wordListContainer: LinearLayout
    private lateinit var hotelPriceTextView: TextView
    private lateinit var hotelRatingTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // 启用全屏显示
        setContentView(R.layout.activity_hotel_detail)

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

        // 设置返回按钮的点击事件
        val backButton: ImageButton = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()  // 关闭当前 Activity，返回到 HotelMainActivity
        }

        // 获取从 Intent 传递的 UUID
        val hotelUuid = intent.getStringExtra("hotel_uuid")
        if (hotelUuid != null) {
            fetchHotelDetails(hotelUuid)
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
                            hotelRatingTextView.text = hotelData.rating.toString()
                            hotelAddressTextView.text = listOfNotNull(
                                hotelData.address.block,
                                hotelData.address.streetName,
                                hotelData.address.floorNumber?.let { floor ->
                                    hotelData.address.unitNumber?.let { unit -> "$floor-$unit" } ?: floor
                                },
                                hotelData.address.buildingName,
                                hotelData.address.postalCode
                            ).joinToString(" ").ifEmpty { "Unknown Location" }
//                            hotelFacilitiesTextView.text = hotelData.amenities?.joinToString(", ") ?: "No facilities listed"
                            hotelPriceTextView.text = hotelData.leadInRoomRates ?: "Price not available"

                            val firstImageUuid = hotelData.thumbnails?.firstOrNull()?.libraryUuid
                            if (firstImageUuid != null) {
                                fetchHotelImage(firstImageUuid, apiService)
                            }

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
                                wordListContainer.addView(wordTextView)
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

    private fun fetchHotelImage(uuid: String, apiService: AccommodationApiService) {
        val apiKey = "pRUJyzyyzpRd557ynCs7JRZtoKYr6PPC"
        val imageCall = apiService.getMediaDetails(apiKey, uuid)
        imageCall.enqueue(object : Callback<AccommodationImageResponse> {
            override fun onResponse(
                call: Call<AccommodationImageResponse>,
                response: Response<AccommodationImageResponse>
            ) {
                val imageUrl = response.body()?.data?.firstOrNull()?.url
                runOnUiThread {
                    Glide.with(this@HotelDetailActivity)
                        .load(imageUrl)
                        .placeholder(R.drawable.hotel_container_product_image)
                        .into(hotelImageView)
                }
            }

            override fun onFailure(call: Call<AccommodationImageResponse>, t: Throwable) {
                Toast.makeText(
                    this@HotelDetailActivity,
                    "Failed to load hotel hotel_image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }





}
