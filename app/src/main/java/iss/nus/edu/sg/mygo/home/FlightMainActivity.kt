package iss.nus.edu.sg.mygo.home

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import iss.nus.edu.sg.mygo.R

/**
 * @ClassName FlightMainActivity
 * @Description
 * @Author YAO YIYANG
 * @StudentID A0294873L
 * @Date 2025/2/11
 * @Version 1.0
 */

class FlightMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_flight_main)

        // 处理窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_flights)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageButton>(R.id.button_back)
        val destinationInput = findViewById<EditText>(R.id.text_lahore)

        // 监听搜索按钮
        findViewById<LinearLayout>(R.id.container_search_button).setOnClickListener {
            val destination = destinationInput.text.toString()

            // 创建 Intent 传递数据到 HotelSearchActivity
            val intent = Intent(this, HotelSearchActivity::class.java).apply {
                putExtra("destination", destination)
            }
            startActivity(intent)
        }

        // 监听返回按钮点击事件
        backButton.setOnClickListener {
            finish()
        }
    }

}