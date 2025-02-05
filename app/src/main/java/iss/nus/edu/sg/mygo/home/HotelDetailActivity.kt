package iss.nus.edu.sg.mygo.home

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import iss.nus.edu.sg.mygo.R

class HotelDetailActivity : AppCompatActivity() {

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

        // 获取从 Intent 传递的数据
        val hotelName = intent.getStringExtra("hotelName") ?: "Unknown Hotel"
        val hotelImageResId = intent.getIntExtra("hotelImage", R.drawable.container_product_image)
        val hotelDescription = intent.getStringExtra("hotelDescription") ?: "No description available."

        // 绑定数据到 UI 组件
        val hotelNameTextView: TextView = findViewById(R.id.txt_hotel_name)
        val hotelDescriptionTextView: TextView = findViewById(R.id.txt_product_info_description)
        val hotelImageView: ImageView = findViewById(R.id.container_product_image)

        hotelNameTextView.text = hotelName
        hotelDescriptionTextView.text = hotelDescription
        hotelImageView.setImageResource(hotelImageResId)

        // 设置返回按钮的点击事件
        val backButton: ImageButton = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()  // 关闭当前 Activity，返回到 MainActivity
        }
    }
}
