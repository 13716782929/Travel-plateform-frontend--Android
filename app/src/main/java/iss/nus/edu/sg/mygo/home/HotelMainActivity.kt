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
 * @ClassName HotelMainActivity
 * @Description
 * @Author YAO YIYANG
 * @StudentID A0294873L
 * @Date 2025/2/11
 * @Version 1.0
 */

class HotelMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hotel_main)

        // 处理窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_hotel)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 获取用户输入的 EditText
        val destinationInput = findViewById<EditText>(R.id.text_value_destination)
        val backButton = findViewById<ImageButton>(R.id.button_back)

        // 监听搜索按钮
        findViewById<LinearLayout>(R.id.button_search).setOnClickListener {
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
