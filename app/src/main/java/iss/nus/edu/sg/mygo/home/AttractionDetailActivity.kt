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
import iss.nus.edu.sg.mygo.api.models.AttractionResponse
import iss.nus.edu.sg.mygo.api.service.AttractionApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AttractionDetailActivity : AppCompatActivity() {

    private lateinit var attractionNameTextView: TextView
    private lateinit var attractionDescriptionTextView: TextView
    private lateinit var attractionImageView: ImageView
    private lateinit var attractionAddressTextView: TextView
    private lateinit var wordListContainer: LinearLayout
    private lateinit var attractionPriceTextView: TextView
    private lateinit var attractionRatingTextView: TextView

    private lateinit var apiService: AttractionApiService

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
        attractionDescriptionTextView = findViewById(R.id.txt_product_info_description)
        attractionImageView = findViewById(R.id.container_product_image)
        attractionAddressTextView = findViewById(R.id.txt_hotel_address)
        wordListContainer = findViewById(R.id.word_list_container)
        attractionPriceTextView = findViewById(R.id.txt_price_value)
        attractionRatingTextView = findViewById(R.id.txt_review_count)

        // 设置返回按钮的点击事件
        val backButton: ImageButton = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()  // 关闭当前 Activity，返回上一个页面
        }

        // 初始化 API Service
        apiService = AttractionApiService.create()

        // 获取从 Intent 传递的 UUID
        val attractionUuid = intent.getStringExtra("attraction_uuid")
        if (attractionUuid != null) {
            fetchAttractionDetails(attractionUuid)
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

                            //  加载景点图片
                            val imageUrl = "http://10.0.2.2:8080/proxy/media/${attractionData.images?.firstOrNull()?.uuid}"
                            Glide.with(this@AttractionDetailActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.attraction_placeholder_image)
                                .error(R.drawable.attraction_placeholder_image)
                                .into(attractionImageView)

                            // 清空已有的设施列表，防止重复添加
                            wordListContainer.removeAllViews()

                            //  添加 "Tags" 标题
                            val tagsTitle = TextView(this@AttractionDetailActivity).apply {
                                text = "Tags"
                                textSize = 18f
                                setTextColor(Color.parseColor("#FF232323"))
                                setTypeface(null, Typeface.BOLD)
                            }
                            wordListContainer.addView(tagsTitle)

                            //  解析标签并动态添加
                            attractionData.tags?.forEach { tag ->
                                val wordTextView = TextView(this@AttractionDetailActivity).apply {
                                    text = "· $tag"
                                    textSize = 16f
                                    setTextColor(Color.BLACK)
                                }
                                wordListContainer.addView(wordTextView)
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
}
