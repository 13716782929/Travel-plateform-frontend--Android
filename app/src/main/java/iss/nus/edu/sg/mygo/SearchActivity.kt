package iss.nus.edu.sg.mygo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // 获取 Intent 传递的数据
        val checkInDate = intent.getStringExtra("checkInDate") ?: "Not Provided"
        val nights = intent.getStringExtra("nights") ?: "Not Provided"

        // 获取 UI 组件
        val checkInDateTextView = findViewById<TextView>(R.id.text_check_in_date)
        val nightsTextView = findViewById<TextView>(R.id.text_nights)
        val backButton = findViewById<ImageButton>(R.id.button_back)

        // 显示传递的数据
        checkInDateTextView.text = "$checkInDate"
        nightsTextView.text = "$nights"

        // 监听返回按钮点击事件
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
