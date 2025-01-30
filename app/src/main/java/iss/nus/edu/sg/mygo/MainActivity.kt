package iss.nus.edu.sg.mygo

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 处理窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_hotel)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 获取用户输入的 EditText
        val destinationInput = findViewById<EditText>(R.id.text_value_destination)
        val checkInInput = findViewById<EditText>(R.id.container_additional_info1)
        val nightsInput = findViewById<EditText>(R.id.container_additional_info2)
        val priceFilterInput = findViewById<EditText>(R.id.container_additional_info6)

        // 监听搜索按钮
        findViewById<LinearLayout>(R.id.button_search).setOnClickListener {
            val destination = destinationInput.text.toString()
            val checkInDate = checkInInput.text.toString()
            val nights = nightsInput.text.toString()
            val priceFilter = priceFilterInput.text.toString()

            // 创建 Intent 传递数据到 SearchActivity
            val intent = Intent(this, SearchActivity::class.java).apply {
                putExtra("destination", destination)
                putExtra("checkInDate", checkInDate)
                putExtra("nights", nights)
                putExtra("priceFilter", priceFilter)
            }
            startActivity(intent)
        }

        // 监听 container_frame（Explore Aspen）点击事件
        findViewById<LinearLayout>(R.id.container_frame).setOnClickListener {
            val intent = Intent(this, HotelDetailActivity::class.java).apply {
                putExtra("hotelName", "Explore Aspen")
                putExtra("hotelImage", R.drawable.image_rectangle)
            }
            startActivity(intent)
        }

        // 监听 container_frame4（Luxurious Aspen）点击事件
        findViewById<LinearLayout>(R.id.container_frame4).setOnClickListener {
            val intent = Intent(this, HotelDetailActivity::class.java).apply {
                putExtra("hotelName", "Luxurious Aspen")
                putExtra("hotelImage", R.drawable.image_rectangle1)
            }
            startActivity(intent)
        }
    }
}
